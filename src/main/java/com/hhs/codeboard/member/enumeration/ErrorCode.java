package com.hhs.codeboard.member.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    DUPLICATE_ID("30001","이미 등록된 아이디 입니다"),
    UNKNOWN("30002","알수없는 오류가 발생했습니다.")
    ;
    private final String code;
    private final String description;

    public String getErrorMessageAsJsonString() {
        return String.format("{message:\"%s\"}", this.description);
    }

}
