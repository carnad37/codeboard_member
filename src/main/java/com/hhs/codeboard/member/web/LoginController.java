package com.hhs.codeboard.member.web;

import com.hhs.codeboard.member.dto.AuthDto;
import com.hhs.codeboard.member.dto.User;
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

    @PostMapping("/login")
    public Mono<ResponseEntity<String>> login(User user) {
        // 전달된 정보로 로그인을 진행

        // 쿠키굽고, Response에 accesstoken 담아감


        return null;
    }


    @PostMapping("/auth")
    public Mono<AuthDto> authorized(AuthDto authDto) {


        return null;
    }


}
