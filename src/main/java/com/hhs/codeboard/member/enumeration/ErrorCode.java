package com.hhs.codeboard.member.enumeration;

import com.hhs.codeboard.member.data.user.dto.response.CommonResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Mono;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    DUPLICATE_ID("30001","이미 등록된 아이디 입니다."),
    UNKNOWN("30002","알수없는 오류가 발생했습니다."),
    RE_CAPTCHA("30003","reCAPTCHA인증에 실패하였습니다"),

    // 31*** 회원가입관련 오류
    JOIN_FAIL("31000","회원가입에 실패하였습니다."),
    JOIN_INCORRECT_EMAIL("31001","잘못된 이메일입니다."),
    JOIN_INCORRECT_PASSWD("31002","잘못된 비밀번호입니다."),
    JOIN_INCORRECT_NICKNAME("31003","잘못된 닉네임입니다."),
    ;

    private final String code;
    private final String description;

    public String getErrorMessageAsJsonString() {
        return String.format("{message:\"%s\"}", this.description);
    }

}
