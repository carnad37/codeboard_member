package com.hhs.codeboard.member.auth;

import com.hhs.codeboard.member.data.AuthDto;
import com.hhs.codeboard.member.data.User;
import com.hhs.codeboard.member.data.user.dto.UserInfoDto;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * 기본이되는 인증서비스 인터페이스
 */
public interface TokenAuthService {

    /**
     * 로그인 인증 통과후 refreshToken및 accessToken생성
     * @param exchange
     * @return
     */
    Mono<AuthDto> tokenProcess(String accessToken, String refreshToken);

    /**
     * 인가
     * 토큰을 통해 인증이 이루어지며,
     * 인증된 정보를 헤더및 쿠키에 담아줌.
     *
     * Gateway에서 사용하는게 바람직.
     * @param exchange
     * @return
     */
    Mono<AuthDto> authorization(ServerWebExchange exchange);

    /**
     * 인증
     * 토큰을 통해 인증이 이루어지며,
     * 인증된 정보를 헤더및 쿠키에 담아줌.
     * @param exchange
     * @param loginProcess
     * @return
     */
    Mono<AuthDto> authentication(ServerWebExchange exchange, Consumer<AuthDto> loginProcess);

    /**
     * accessToken 생성
     * @param email
     * @return
     */
    String getAccessToken(String email);

    /**
     * refreshToken 생성
     * @param email
     * @return
     */
    Mono<String> getRefreshToken(String email);

}
