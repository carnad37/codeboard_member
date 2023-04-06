package com.hhs.codeboard.member.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.common.collect.ImmutableMap;
import com.hhs.codeboard.member.data.AuthDto;
import com.hhs.codeboard.member.data.User;
import com.hhs.codeboard.member.data.user.dto.UserInfoDto;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * 동적으로 생성되므로 따로 Bean으로 등록하지 않는다.
 */
public class JwtTokenAuthService implements TokenAuthService {

    private Algorithm algorithm;
    private AlgorithmSupporter.AlgorithmType algorithmType = AlgorithmSupporter.AlgorithmType.RS;
    private AlgorithmSupporter.AlgorithmKeySize algorithmKeySize = AlgorithmSupporter.AlgorithmKeySize._384;
    private ReactiveRedisOperations<String, String> redisUserOperation;
    private WebClient userClient;
    private Map<String, Object> header;
    private JWTVerifier verifier;
    private ZoneId zoneId = ZoneId.of("Asia/Seoul");

    private String accessTokenName = "CB_AT";
    private String refreshTokenName = "CB_RT";

    private final AuthDto EMPTY_AUTH = new AuthDto();

    /**
     * 코드상에서 key 생성
     * 기존 키값들은 만료되는 문제가 있다.
     * @param algorithmDto
     * @throws NoSuchAlgorithmException
     */
    public JwtTokenAuthService(AlgorithmSupporter.AlgorithmDto algorithmDto, WebClient userClient, ReactiveRedisOperations<String, User> redisOperations) throws NoSuchAlgorithmException {
        if (algorithmDto.getSize() != null && algorithmDto.getType() != null) {
            // 기존키 사용
            this.algorithmType = algorithmDto.getType();
            this.algorithmKeySize = algorithmDto.getSize();

            this.algorithm = algorithmType.getAlgorithm(algorithmDto.getSize(), algorithmDto);
        } else if (algorithmDto.getSize() != null || algorithmDto.getType() != null) {
            // 수동키 생성
            this.algorithmType = algorithmDto.getType() == null ? this.algorithmType : algorithmDto.getType();
            this.algorithmKeySize = algorithmDto.getSize() == null ? this.algorithmKeySize : algorithmDto.getSize();

            KeyPairGenerator kpg = KeyPairGenerator.getInstance(this.algorithmType.getInstanceType());
            kpg.initialize(this.algorithmKeySize.getSize());
            KeyPair kp = kpg.generateKeyPair();

            this.algorithm = Algorithm.RSA256((RSAPublicKey) kp.getPublic(), (RSAPrivateKey) kp.getPrivate());
        }
        if (this.algorithm == null) throw new NoSuchAlgorithmException("not found algorithm");

        // user 정보 획득용 redis operation 세팅 및 webclient 초기화
        this.redisUserOperation = redisUserOperation;
        this.userClient = userClient;

        // 헤더 초기화
        this.header = ImmutableMap.<String, Object>builder()
                .put(JWTheader.typ.name(), "JWT")
                .put(JWTheader.alg.name(), this.algorithmType.name() + this.algorithmKeySize.getSize())
                .build();

        // 토큰 유효성 체커
        this.verifier = JWT.require(algorithm)
//                .withIssuer("codeboard")
//                .acceptIssuedAt(TimeUnit.MINUTES.toMillis(30))
                .build();
    }


    @Override
    public Mono<String> getAccessToken(String email) {
        // 토큰 생성
        return Mono.fromSupplier(()->JWT.create().withHeader(this.header)
                .withSubject(email)
                .withIssuedAt(Instant.now().atZone(this.zoneId).toInstant())
                .sign(algorithm));
    }

    @Override
    public Mono<String> getRefreshToken(String email) {
        String refreshToken = UUID.randomUUID().toString().replaceAll("-", "");
        return redisUserOperation.opsForValue().set(refreshToken, email, Duration.ofDays(7))
                .map((bool)->{
                    if (bool) {
                        return refreshToken;
                    } else {
                        return null;
                    }
                });
    }

    /**
     * 로그인시 요청처리
     * @param exchange
     */
    @Override
    public Mono<AuthDto> tokenProcess(String email) {
        // 회원정보 조
//        return Mono.fromSupplier(()->{
//          AuthDto authDto = new AuthDto();
//          authDto.setAccessToken(this.getAccessToken(email));
//          authDto.setRefreshToken(UUID.randomUUID().toString());
//        });
    }


    @Override
    public void authorization(ServerWebExchange exchange) {
        // 토큰 파싱을 통해 유저정보 획득
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        //TODO :: 각각의 오류별로 처리할 필요가있음.

        //무조건 헤더의 auth정보 초기화
        exchange = this.clearUserInfoInHeader(exchange);

        // 인증에 필요한 토큰정보가 없을경우(has no cookie or has no access token)
        // => 인증없이 진행함.
        Optional.ofNullable(request.getCookies())
                .map(cookies -> cookies.getFirst("CB_AT"))
                .filter((accessTokenCookie)->StringUtils.hasText(accessTokenCookie.getValue()))
                .ifPresent((accessTokenCookie)->this.tokenProcess(accessTokenCookie.getValue(), request, response));
    }

    /**
     * 인증처리
     * @param accessToken
     * @param refreshToken
     * @return
     */
    @Override
    public Mono<AuthDto> authentication(String accessToken, String refreshToken) {
        DecodedJWT decodedJWT;
        String principle = null;
        try {
            decodedJWT = verifier.verify(accessToken);
            principle = decodedJWT.getSubject();
        } catch (JWTVerificationException je) {
            return Mono.just(EMPTY_AUTH);
        }
        // 파싱한 토큰의 정보로 만료된 토큰인지 위조된 토큰인지를 확인한다.
        // 생성된지 30분 이상된 토큰인지 확인
        if (!Instant.now().isAfter(decodedJWT.getIssuedAtAsInstant().plus(30, ChronoUnit.MINUTES))) {
            // 만료된 accessToken일경우, refresh토큰으로 체크
            return redisUserOperation.opsForValue().get(refreshToken)
                .flatMap(email-> Mono.zip(this.getAccessToken(email), this.getRefreshToken(email))
                    .flatMap(tup->
                        Mono.fromSupplier(()->{
                            AuthDto authDto = new AuthDto();
                            authDto.setAccessToken(tup.getT1());
                            authDto.setRefreshToken(tup.getT2());
                            return authDto;
                        })
                    )
                );
        }
        // 회원정보를 담아서 리턴
        return Mono.fromSupplier(()->{
            AuthDto autoDto = new AuthDto();
            autoDto.setEmail(decodedJWT.getSubject());
        });

    }


    protected enum JWTheader {
        typ, alg;
    }


    private String getUserSeq(DecodedJWT decodedJWT) {
        return decodedJWT.getAudience().get(0);
    }

    // 토큰쿠키를 굽자!
    private void bakeTokenCookie(String accessToken, String refreshToken, ServerHttpResponse response) {
        if (StringUtils.hasText(accessToken)) {
            ResponseCookie accessTokenCookie = this.makeCookie(this.accessTokenName, accessToken);
            response.addCookie(accessTokenCookie);
        }
        if (StringUtils.hasText(refreshToken)) {
            ResponseCookie refreshTokenCookie = this.makeCookie(this.refreshTokenName, refreshToken);
            response.addCookie(refreshTokenCookie);
        }
    }

    /**
     * 쿠키 생성
     * TODO :: 쿠키 생성 기본옵션 설정가능하게 수정??
     * @param name
     * @param value
     * @return
     */
    private ResponseCookie makeCookie(String name, String value) {
        return ResponseCookie.from(name, value)
                .httpOnly(true)
                .maxAge(Duration.ofMinutes(30))
//                    .secure(true)
                .sameSite("Strict")
                .path("/")
                .build();
    }

    /**
     * 모듈로 가는 요청 헤더에 인증정보 심기
     * 인증정보가 없는경우 같은 이름의 헤더 덮어써버리기
     */
    private void setUserInfoInHeader(User user, ServerHttpRequest request) {
//        HttpHeaders headers = request.getHeaders().remove();
        request.mutate()
                .header("cb_user_seq", user.getUserSeq())
                .header("cb_email", user.getEmail())
                .header("cb_user_type", user.getUserType())
                .header("cb_nickname", user.getNickName())
                .header("cb_auth", user.getAuthList().stream().toArray(String[]::new));
    }
    /**
     * 모듈로 가는 요청 헤더에 인증정보 심기
     * 인증정보가 없는경우 같은 이름의 헤더 덮어써버리기
     */
    private ServerWebExchange clearUserInfoInHeader(ServerWebExchange exchange) {
        ServerHttpRequest request = exchange.getRequest().mutate()
                .header("cb_user_seq", "")
                .header("cb_email", "")
                .header("cb_user_type", "")
                .header("cb_nickname", "")
                .header("cb_auth", "")
                .build();
        return exchange.mutate().request(request).build();
    }
}
