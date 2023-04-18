package com.hhs.codeboard.member.conf.security;

import com.hhs.codeboard.member.data.AuthDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * Header 에서 인증정보를 확인하는 로직
 */

@Slf4j
@RequiredArgsConstructor
public class AuthFilter implements WebFilter {

    private String authorizedHeaderName = "X-USER-INFO";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        // 헤더에 담겨서온 user정보 파싱
        String email = exchange.getRequest().getHeaders().getFirst(authorizedHeaderName);
        if (StringUtils.hasText(email)) {
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(AuthDto.builder().email(email).build(), null, null);
            return chain.filter(exchange).contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth));
        } else {
            return chain.filter(exchange);
        }
    }

}
