package com.hhs.codeboard.member.web.controller;

import com.hhs.codeboard.member.data.AuthDto;
import com.hhs.codeboard.member.data.User;
import com.hhs.codeboard.member.data.user.dto.UserInfoDto;
import com.hhs.codeboard.member.data.user.dto.request.UserInfoRequest;
import com.hhs.codeboard.member.data.user.dto.response.CommonResponse;
import com.hhs.codeboard.member.data.user.entity.UserInfoEntity;
import com.hhs.codeboard.member.enumeration.ErrorCode;
import com.hhs.codeboard.member.expt.AppException;
import com.hhs.codeboard.member.web.service.RecaptchaService;
import com.hhs.codeboard.member.web.service.UserInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * 인증용 컨트롤러
 */
@RequestMapping("/public/user")
@RestController
@RequiredArgsConstructor
public class LoginController {


    private final UserInfoService userInfoService;

    private final RecaptchaService recaptchaService;

    @PostMapping("/login")
    public Mono<AuthDto> userInfo(@RequestBody UserInfoDto user) throws Exception {
        return userInfoService.loginUser(user.getEmail(), user.getPasswd());
    }

    @PostMapping("/save")
    public Mono<CommonResponse<UserInfoDto>> saveUser(@RequestBody UserInfoRequest userData) {
        /**
         * 1. 직접 가입 : reCAPTCHA로 회원가입 요청까지 통과. 통과된 회원정보는 Redis에 1~5hours간격정도로 만료시간을 설정해 저장함. 중복 방지를 위해 key는 이메일로 진행.
         * 2. 연동 가입 : google이나 네이버등 연동 가입만을 허용해둔다. 다른 포털에 이미 가입된 계정이기에, 쓸데없는 인증과정이 필요가 없다.
         */
        // reCaptcha로 처리
        return recaptchaService.checkRecaptcha(userData.getToken())
                .flatMap(bool-> {
                    if (bool) {
                        // 통과
                        Mono<UserInfoDto> result = userInfoService.saveUser(userData);
                        return result.map(CommonResponse::new);
                    } else {
                        // 실패
                        return Mono.just(new CommonResponse<>(HttpStatus.BAD_REQUEST.value(), "0000", "reCAPTCHA인증에 실패하였습니다", true));
                    }
                });

    }


}
