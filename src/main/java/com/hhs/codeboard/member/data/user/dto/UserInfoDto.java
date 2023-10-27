package com.hhs.codeboard.member.data.user.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserInfoDto {

    private String email;

    private String nickname;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String passwd;

    private String userType;

    private Long userSeq;

    @JsonIgnore
    private String modUser;

    private LocalDateTime regDate;

    private LocalDateTime modDate;

    private LocalDateTime delDate;
    
}
