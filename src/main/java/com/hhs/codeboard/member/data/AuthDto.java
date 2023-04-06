package com.hhs.codeboard.member.data;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthDto {

    private String email;
    private String passwd;
    private String refreshToken;
    private String accessToken;

}