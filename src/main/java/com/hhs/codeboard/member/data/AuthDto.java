package com.hhs.codeboard.member.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthDto {

    private String email;
    private String passwd;
    private String nickname;
    private Long userSeq;
    private String refreshToken;
    private String accessToken;
    private String message;

}