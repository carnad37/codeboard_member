package com.hhs.codeboard.member.web.controller;

import com.hhs.codeboard.member.data.AuthDto;
import com.hhs.codeboard.member.data.User;
import com.hhs.codeboard.member.data.user.dto.UserInfoDto;
import com.hhs.codeboard.member.web.service.UserInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * 인증용 컨트롤러
 */
@RequestMapping("/private")
@RestController
public class LoginController {

    @Autowired
    private UserInfoService userInfoService;

    @PostMapping("/login")
    public Mono<UserInfoDto> login(User user) {
        // 전달된 정보로 로그인을 진행
        // 쿠키굽고, Response에 accesstoken 담아감

        return userInfoService.selectUser(Long.parseLong(user.getUserSeq()));
    }


    @PostMapping("/auth")
    public Mono<AuthDto> authorized(AuthDto authDto) {


        return null;
    }


}
