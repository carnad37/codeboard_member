package com.hhs.codeboard.member.web.controller;

import com.hhs.codeboard.member.data.AuthDto;
import com.hhs.codeboard.member.data.user.dto.UserInfoDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RequestMapping("/gw")
@RestController
public class GwController {

//    @GetMapping("/login")
//    public Mono<AuthDto> userInfo(UserInfoDto user) throws Exception {
//        return userInfoService.loginUser(user.getEmail(), user.getPasswd());
//    }


}
