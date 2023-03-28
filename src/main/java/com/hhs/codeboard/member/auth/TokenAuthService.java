package com.hhs.codeboard.member.auth;

import com.hhs.codeboard.member.data.user.dto.UserInfoDto;
import org.springframework.web.server.ServerWebExchange;

/**
 * 기본이되는 인증서비스 인터페이스
 */
public interface TokenAuthService {

    /**
     * 아이디와 비밀번호를 이용한 로그인 로직
     * @param exchange
     * @return
     */
    void login(ServerWebExchange exchange);

    /**
     * 인가
     * 토큰을 통해 인증이 이루어지며,
     * 인증된 정보를 헤더및 쿠키에 담아줌.
     *
     * Gateway에서 사용하는게 바람직.
     * @param exchange
     * @return
     */
    void authorization(ServerWebExchange exchange);

    /**
     * 인증
     * 토큰을 통해 인증이 이루어지며,
     * 인증된 정보를 헤더및 쿠키에 담아줌.
     * @param token
     * @return
     */
    UserInfoDto authentication(String accessToken, String refreshToken);


}
