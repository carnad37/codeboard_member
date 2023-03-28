package com.hhs.codeboard.member.data.user.dto.request;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserInfoRequest {

    private String email;

    private String nickname;

    private String passwd;

    private String userType;

}
