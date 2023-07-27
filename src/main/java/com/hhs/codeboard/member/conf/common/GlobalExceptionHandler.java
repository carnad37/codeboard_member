package com.hhs.codeboard.member.conf.common;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hhs.codeboard.member.data.user.dto.response.CommonResponse;
import com.hhs.codeboard.member.enumeration.ErrorCode;
import com.hhs.codeboard.member.expt.AppException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Order(-1)
@Slf4j
@RequiredArgsConstructor
public class GlobalExceptionHandler implements ErrorWebExceptionHandler {

    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        // 여기서 ex의 클래스 체크를해서 분기해도 되나... 원하는거랑은 좀 다른듯
        ErrorCode errorCode;
        if (ex instanceof AppException ae) {
            errorCode = ae.getErrorCode();
        } else {
            errorCode = ErrorCode.UNKNOWN;
        }
        CommonResponse<?> result = new CommonResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), errorCode.getCode(), errorCode.getDescription(), true);
        String error;
        try {
            error = objectMapper.writeValueAsString(result);
        } catch (JsonProcessingException e) {
            error = errorCode.getErrorMessageAsJsonString();
        }

        ServerHttpResponse response = exchange.getResponse();

        DataBuffer dataBuffer = response.bufferFactory().wrap(error.getBytes());
        // response header 수정
        response.setStatusCode(HttpStatus.OK);
        // header 강제 주입
        response.getHeaders().add(HttpHeaders.CONTENT_TYPE, String.valueOf(MediaType.APPLICATION_JSON));
        return response.writeWith(Mono.just(dataBuffer));
    }
}
