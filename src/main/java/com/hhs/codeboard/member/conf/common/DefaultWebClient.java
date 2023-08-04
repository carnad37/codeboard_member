package com.hhs.codeboard.member.conf.common;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * Webclient 디폴트 설정.
 */
@Configuration
public class DefaultWebClient {

    @Value("${codeboard.recaptcha.url}")
    private String recaptchaUrl;

    @Bean(name = "recaptchaClient")
    public WebClient recaptchaClient() {
        return webClientBuild(recaptchaUrl);
    }

    private WebClient webClientBuild(String url) {
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000)                                              // 타임 아웃 시간
                .responseTimeout(Duration.ofMillis(3000))                                                        // 응담 시간 제한
                .doOnConnected(conn ->
                    conn.addHandlerLast(new ReadTimeoutHandler(3000, TimeUnit.MILLISECONDS))        //데이터 읽는 여유시간
                            .addHandlerLast(new WriteTimeoutHandler(3000, TimeUnit.MILLISECONDS))   //데이터 담는 시간
                );
        return WebClient.builder()
                .baseUrl(url)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }

}
