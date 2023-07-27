package com.hhs.codeboard.member.data.user.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.hhs.codeboard.member.enumeration.ErrorCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;

import java.util.List;

@Getter
public class CommonResponse<T> {

    /**
     * 리스트 데이터 응답용
     * @param page
     */
    public CommonResponse(Page<T> page) {
        this.dataList = page.getContent();
        this.contentSize = page.getTotalElements();
        this.pageSize = page.getTotalPages();
    }

    public CommonResponse(Page<T> page, String message) {
        this(page);
        this.message = message;
    }

    public CommonResponse(Page<T> page, String message, boolean alertFlag) {
        this(page, message);
        this.alertFlag = alertFlag;
    }

    /**
     * 단일 컨텐츠 응답용
     * @param data
     */
    public CommonResponse(T data) {
        this.data = data;
    }

    public CommonResponse(T data, String message) {
        this(data);
        this.message = message;
    }

    public CommonResponse(T data, String message, boolean alertFlag) {
        this(data, message);
        this.alertFlag = alertFlag;
    }

    /**
     * 에러등 직접 응답 생성용
     * @param httpCode
     * @param errorCode
     * @param message
     * @param alertFlag
     */
    public CommonResponse(int httpCode, String errorCode, String message, boolean alertFlag) {
        this.httpCode = httpCode;
        this.message = message;
        this.alertFlag = alertFlag;
        this.errorCode = errorCode;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long contentSize;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer pageSize;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<T> dataList;

    private int httpCode = HttpStatus.OK.value();
    private String message = "";
    private boolean alertFlag = false;
    private String errorCode = "00000";

}

