package com.hhs.codeboard.member.web.controller;

import com.hhs.codeboard.member.data.AuthDto;
import com.hhs.codeboard.member.data.user.dto.UserInfoDto;
import com.hhs.codeboard.member.web.service.UserInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 인증용 컨트롤러
 */
@RequestMapping("/private/user")
@RestController
@Slf4j
@RequiredArgsConstructor
public class MemberController {
    private final UserInfoService userInfoService;

    /**
     * 본인 회원 정보
     */
    @GetMapping("/selfInfo")
    public Mono<UserInfoDto> selfInfo(@AuthenticationPrincipal AuthDto authDto) throws Exception {
        return userInfoService.selectUser(authDto.getEmail());
    }

    /**
     * 타 회원 정보
     * @param user
     * @param exchange
     * @return
     * @throws Exception
     */
    @GetMapping("/userInfo")
    public Mono<UserInfoDto> userInfo(AuthDto user, ServerWebExchange exchange) throws Exception {
         return userInfoService.selectUser(user.getEmail());
    }

}
