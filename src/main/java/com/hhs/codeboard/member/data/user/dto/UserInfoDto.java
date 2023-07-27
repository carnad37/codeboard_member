package com.hhs.codeboard.member.data.user.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserInfoDto {

    private String email;

    private String nickname;

    private String passwd;

    private String userType;

    @JsonIgnore
    private String modUser;

    private LocalDateTime regDate;

    private LocalDateTime modDate;

    private LocalDateTime delDate;
    
}
