package com.hhs.codeboard.member.expt;

import com.hhs.codeboard.member.data.user.dto.response.CommonResponse;
import com.hhs.codeboard.member.enumeration.ErrorCode;
import lombok.experimental.UtilityClass;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Mono;

@UtilityClass
public class ErrorHandleUtil {

    public static <T> Mono<CommonResponse<T>> errorResponse(Throwable error) {
        if (error instanceof AppException appException) {
            ErrorCode errorCode = appException.getErrorCode();
            return errorResponse(errorCode);
        } else {
            return errorResponse(ErrorCode.UNKNOWN);
        }
    }

    public static <T> Mono<CommonResponse<T>> errorResponse(ErrorCode errorCode) {
        CommonResponse<T> result = new CommonResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), errorCode.getCode(), errorCode.getDescription(), true);
        return Mono.fromSupplier(()->result);
    }

}
