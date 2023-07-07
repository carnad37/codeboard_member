package com.hhs.codeboard.member.data.user.dto.request;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserInfoRequest {

    private String email;

    private String nickname;

    private String passwd;

    private String userType;

}
