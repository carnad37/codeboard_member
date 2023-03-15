package com.hhs.codeboard.member.auth;

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
     * 토큰을 통해 인증이 이루워짐.
     * 토큰 파싱을 해도좋고, 토큰으로 redis든 rdb든 어떻게든 인증로직 구현하면됨.
     * @param exchange
     * @return
     */
    void authorized(ServerWebExchange exchange);

}
