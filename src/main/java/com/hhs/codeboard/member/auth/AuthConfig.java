package com.hhs.codeboard.member.auth;

import com.hhs.codeboard.member.data.User;
import com.hhs.codeboard.member.expt.InitiationFailPemkey;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Configuration
public class AuthConfig {

    @Value("${codeboard.access.token.key.size:256}")
    private Integer keySize = 256;

    @Value("${spring.reactive.redis.host:localhost}")
    private String redisHost;

    @Value("${spring.reactive.redis.host:6379}")
    private Integer redisPort;

//    난수토큰 사용안함. 디폴트를 redis + jwt로함
//    @Value("${codeboard.access.type:JWT}")
//    private AuthTokenType authType;

//    @Value("codeboard.access.token.key.algorithm:HS")
    private AlgorithmSupporter.AlgorithmType algorithm = AlgorithmSupporter.AlgorithmType.HS;

    @Value("${codeboard.access.token.key.public:/}")
    private String publicKeyPath;

    @Value("${codeboard.access.token.key.private:/}")
    private String privateKeyPath;

    @Value("${codeboard.access.token.hmac.key:secretKey}")
    private String secretKey;

    @Bean
    public WebClient userModuleConnector() {
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .responseTimeout(Duration.ofMillis(5000))
                .doOnConnected(conn ->
                    conn.addHandlerLast(new ReadTimeoutHandler(5000, TimeUnit.MILLISECONDS))
                    .addHandlerLast(new WriteTimeoutHandler(5000, TimeUnit.MILLISECONDS))
                );

        return WebClient.builder()
                .baseUrl("http://localhost:8080")
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }

    /**
     * 유저 정보 조회용 redis template 생성
     * @param factory
     * @return
     */
    @Bean
    public ReactiveRedisOperations<String, String> redisUserOperations(ReactiveRedisConnectionFactory factory) {
        RedisSerializer<String> serializer = new StringRedisSerializer();
        Jackson2JsonRedisSerializer jsonRedisSerializer = new Jackson2JsonRedisSerializer<>(String.class);
        RedisSerializationContext<String, String> serializationContext = RedisSerializationContext
                .<String, String>newSerializationContext()
                .key(serializer)
                .value(jsonRedisSerializer)
                .hashKey(serializer)
                .hashValue(jsonRedisSerializer)
                .build();

        return new ReactiveRedisTemplate<>(factory, serializationContext);
    }

    /**
     * 따로 Auth Type이 추가되지 않는이상 JWT만 추가.
     * 만일 추가될경우 분기두고 TokenAuthService 구현체로 추가구현만 하면됨.
     */
    @Bean
    public TokenAuthService tokenAuthService(WebClient userClient, ReactiveRedisOperations<String, String> redisUserOperations) {
        AlgorithmSupporter.AlgorithmKeySize keySize = AlgorithmSupporter.AlgorithmKeySize.findBySize(this.keySize);
        AlgorithmSupporter.AlgorithmDto algorithmDto =
                new AlgorithmSupporter.AlgorithmDto(secretKey, publicKeyPath, privateKeyPath, this.algorithm, keySize);
        try {
            try {
                // 생성자는 2종류.
                return new JwtTokenAuthService(algorithmDto, userClient, redisUserOperations);
            } catch (InitiationFailPemkey ie) {
                //key file 문제로 bean 생성에 실패했을경우, 자바 내에서 키생성
                return new JwtTokenAuthService(new AlgorithmSupporter.AlgorithmDto(this.algorithm), userClient, redisUserOperations);
            }
        } catch (NoSuchAlgorithmException e) {
            //algorithm으로 인한 문제는 일괄처리
            throw new BeanCreationException("fail initiation JWT auth service");
        }
    }
}
