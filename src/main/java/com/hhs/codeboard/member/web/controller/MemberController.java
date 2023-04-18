package com.hhs.codeboard.member.web.controller;

import com.hhs.codeboard.member.data.AuthDto;
import com.hhs.codeboard.member.data.user.dto.UserInfoDto;
import com.hhs.codeboard.member.web.service.UserInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

    @GetMapping("/userInfo")
    public Mono<UserInfoDto> userInfo(AuthDto user, ServerWebExchange exchange) throws Exception {
         log.info("user email : {}", user.getEmail());
         log.info("user passwd : {}", user.getPasswd());
         return userInfoService.selectUser(user.getEmail());
    }

}
