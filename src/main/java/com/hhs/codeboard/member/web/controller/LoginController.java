package com.hhs.codeboard.member.web.controller;

import com.hhs.codeboard.member.data.AuthDto;
import com.hhs.codeboard.member.data.User;
import com.hhs.codeboard.member.data.user.dto.UserInfoDto;
import com.hhs.codeboard.member.data.user.dto.request.UserInfoRequest;
import com.hhs.codeboard.member.web.service.UserInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * 인증용 컨트롤러
 */
@RequestMapping("/public/user")
@RestController
public class LoginController {

    @Autowired
    private UserInfoService userInfoService;

    @GetMapping("/login")
    public Mono<AuthDto> userInfo(UserInfoDto user) throws Exception {
        return userInfoService.loginUser(user.getEmail(), user.getPasswd());
    }

    @PostMapping("/save")
    public Mono<UserInfoDto> authorized(User currentUser, UserInfoRequest userData) {
        // FIXME :: 하드코딩
        return userInfoService.saveUser(userData, "test@test.co.kr");
    }



}
