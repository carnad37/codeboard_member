package com.hhs.codeboard.member.data.user.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class UserInfoDto {

    private String email;

    private String nickname;

    private String passwd;

    private String userType;

    private String modUser;

    private LocalDateTime regDate;

    private LocalDateTime modDate;

    private LocalDateTime delDate;
    
}