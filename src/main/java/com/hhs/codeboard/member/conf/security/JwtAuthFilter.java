package com.hhs.codeboard.member.conf.security;

import com.hhs.codeboard.member.auth.TokenAuthService;
import com.hhs.codeboard.member.data.AuthDto;
import com.hhs.codeboard.member.data.User;
import com.hhs.codeboard.member.enumeration.SecurityHeader;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Header 에서 인증정보를 확인하는 로직
 */
@RequiredArgsConstructor
public class JwtAuthFilter implements WebFilter {

    private final TokenAuthService tokenAuthService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        Consumer<AuthDto> loginProcess = (authDto) -> {
//            List<GrantedAuthority> authList = user.getAuthList().stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(authDto, null, null);
            ReactiveSecurityContextHolder.withAuthentication(auth);
        };

        return tokenAuthService.authentication(exchange, loginProcess)
                .flatMap((jwt)->chain.filter(exchange));
    }

}
