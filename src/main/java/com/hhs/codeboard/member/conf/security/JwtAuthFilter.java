package com.hhs.codeboard.member.conf.security;

import com.hhs.codeboard.member.auth.TokenAuthService;
import com.hhs.codeboard.member.data.User;
import com.hhs.codeboard.member.enumeration.SecurityHeader;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class JwtAuthFilter implements WebFilter {

    private final TokenAuthService tokenAuthService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        // 해당 로직은 gw에서 일괄적으로 인증을 시행했을경우 체크됨.
        // 각모듈의 결합도를 낮추기위해 인증은 분리처리.
//        User user = SecurityHeader.getUserFromHeader(exchange);
        tokenAuthService.authorized(exchange);
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
