package com.hhs.codeboard.member.data;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthDto {

    private String refreshToken;
    private String accessToken;
    private User user;

}