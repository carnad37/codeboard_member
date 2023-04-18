package com.hhs.codeboard.member.conf.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;

@Configuration
@EnableWebFluxSecurity
public class WebSecurityConfig {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public ReactiveAuthenticationManager authenticationManager() {
        return new JwtAuthenticationManager();
    }

    @Bean
    public SecurityWebFilterChain filter(ServerHttpSecurity http, ReactiveAuthenticationManager authenticationManager) throws Exception {
        http.authorizeExchange()
                .pathMatchers("/public/**").permitAll()
                .pathMatchers("/webjars/**").permitAll()
                .pathMatchers("/v3/api-docs/**").permitAll()
                .pathMatchers("/private/**").authenticated()
                .pathMatchers("/api").authenticated()
                .pathMatchers("/**").denyAll()
                .and()

                .csrf().disable()
                .formLogin().disable()
                .httpBasic().disable()

                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
                .authenticationManager(authenticationManager)
                .addFilterAt(new AuthFilter(), SecurityWebFiltersOrder.AUTHENTICATION);

        return http.build();
    }
}
