package com.hhs.codeboard.member.web.controller;

import com.hhs.codeboard.member.data.User;
import com.hhs.codeboard.member.data.user.dto.UserInfoDto;
import com.hhs.codeboard.member.data.user.dto.request.UserInfoRequest;
import com.hhs.codeboard.member.web.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * 인증용 컨트롤러
 */
@RequestMapping("/private/user")
@RestController
public class MemberController {

    @Autowired
    private UserInfoService userInfoService;

    @GetMapping("/userInfo")
    public Mono<UserInfoDto> userInfo(UserInfoDto user) throws Exception {
        return userInfoService.selectUser(user.getEmail());
    }


}
