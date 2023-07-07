package com.hhs.codeboard.member.web.controller;

import com.hhs.codeboard.member.data.AuthDto;
import com.hhs.codeboard.member.data.user.dto.UserInfoDto;
import com.hhs.codeboard.member.web.service.UserInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RequestMapping("/gw")
@RestController
@RequiredArgsConstructor
public class GwController {

    private final UserInfoService userInfoService;

    @PostMapping("/login")
    public Mono<AuthDto> userInfo(@RequestBody UserInfoDto user) throws Exception {
        return userInfoService.loginUser(user.getEmail(), user.getPasswd());
    }

}
