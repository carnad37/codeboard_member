package com.hhs.codeboard.member.conf.security;

import com.hhs.codeboard.member.auth.TokenAuthService;
import com.hhs.codeboard.member.conf.security.JwtAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;

@Configuration
@DependsOn("authConfig")
@EnableWebFluxSecurity
public class WebSecurityConfig {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public WebSecurityCustomizer configure() {
        return (web) -> web.ignoring().requestMatchers(
                "/v3/api-docs/**",
                "/swagger-ui/**",
                "/api/v1/login" // 임시
        );
    }

    @Bean
    public SecurityWebFilterChain filter(ServerHttpSecurity http, TokenAuthService tokenAuthService) throws Exception {
        http.authorizeExchange()
//                .pathMatchers("/**").permitAll()
                .pathMatchers("/private/**").authenticated()
                .pathMatchers("/public/**").permitAll()
                .pathMatchers("/**").denyAll()
//                .pathMatchers("/api").authenticated()
                .and()
                .csrf().disable()
                .formLogin().disable()
                .httpBasic().disable()
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
                .addFilterAt(new JwtAuthFilter(tokenAuthService), SecurityWebFiltersOrder.HTTP_BASIC);

//        http.authorizeRequests()
//                .requestMatchers("/**").denyAll()
//                .requestMatchers("/gw/**", "/msa/**").permitAll()   // 모듈간 통신에선 인증을 거치지 않는다.
//                .requestMatchers("/api").authenticated()     // 기본적으로 최소 권한인 NORMAL이 있어야 통신가능하다.
////                .and()
////                .formLogin()
//                .and()
//                .addFilterBefore(tokenFilter(), AbstractAuthenticationProcessingFilter.class)
//                .sessionManagement()
//                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        return http.build();
    }
}
