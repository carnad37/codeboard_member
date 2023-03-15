package com.hhs.codeboard.member.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Getter
public enum SecurityAuthType {

    PRE_MEMBER("임시회원", "P")
    , NORMAL("일반회원", "N")
    , ADMIN("관리자", "A");

    private static final Map<String, SecurityAuthType> authMapByCode =
            Collections.unmodifiableMap(
                    Arrays.stream(values()).collect(
                            Collectors.toConcurrentMap(SecurityAuthType::getCode, Function.identity())
                    )
            );

    private final String title, code;

    public static SecurityAuthType valueOfCode(String code) {
        return authMapByCode.getOrDefault(code, null);
    }

}
