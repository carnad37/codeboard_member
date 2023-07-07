package com.hhs.codeboard.member.expt;

import com.hhs.codeboard.member.enumeration.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class AppException extends RuntimeException {

    public static AppException of (ErrorCode errorCode) {
        return new AppException(errorCode);
    }

    private final ErrorCode errorCode;

}
