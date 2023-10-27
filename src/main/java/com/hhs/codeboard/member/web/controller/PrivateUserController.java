package com.hhs.codeboard.member.web.controller;

import com.hhs.codeboard.member.data.AuthDto;
import com.hhs.codeboard.member.data.user.dto.UserInfoDto;
import com.hhs.codeboard.member.data.user.dto.response.CommonResponse;
import com.hhs.codeboard.member.web.service.UserInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
public class PrivateUserController {
    private final UserInfoService userInfoService;

    /**
     * 본인 회원 정보
     */
    @GetMapping("/selfInfo")
    public Mono<CommonResponse<UserInfoDto>> selfInfo(@AuthenticationPrincipal AuthDto authDto) throws Exception {
        Mono<UserInfoDto> result = userInfoService.selectUser(authDto.getEmail());
        return result.map(CommonResponse::new);
    }

    /**
     * 타 회원 정보
     * @param user
     * @param exchange
     * @return
     * @throws Exception
     */
    @GetMapping("/userInfo")
    public Mono<CommonResponse<UserInfoDto>> userInfo(AuthDto user, ServerWebExchange exchange) throws Exception {
        Mono<UserInfoDto> result = userInfoService.selectUser(user.getEmail());
        return result.map(CommonResponse::new);
    }

}
