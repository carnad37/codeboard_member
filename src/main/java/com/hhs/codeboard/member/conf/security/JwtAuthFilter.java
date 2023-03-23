package com.hhs.codeboard.member.conf.security;

import com.hhs.codeboard.member.data.User;
import com.hhs.codeboard.member.enumeration.SecurityHeader;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Header 에서 인증정보를 확인하는 로직
 */
public class JwtAuthFilter implements WebFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        User user = SecurityHeader.getUserFromHeader(exchange);
        if (StringUtils.hasText(user.getUserSeq()) && !CollectionUtils.isEmpty(user.getAuthList())) {
            //사용자 정보가 없는경우
            exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
            return exchange.getResponse().writeWith(Mono.empty());
        }

        // 유저정보가 있고, 권한 정보가 있을경우에만 통과
        List<GrantedAuthority> authList = user.getAuthList().stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user, null, authList);
        ReactiveSecurityContextHolder.withAuthentication(auth);
        return chain.filter(exchange);
    }

}
