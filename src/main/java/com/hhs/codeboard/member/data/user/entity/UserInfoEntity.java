package com.hhs.codeboard.member.data.user.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("user_info")
@Getter
@Setter
public class UserInfoEntity {

    @Id
    private Long userSeq;   //userSeq는 모듈간의 통신에는 사용되도, 요청이나 응답에는 사용되지 않는다.

    private String email;

    private String nickname;

    private String passwd;

    private String userType;

    private int modUserSeq;

    private LocalDateTime regDate;

    private LocalDateTime modDate;

    private LocalDateTime delDate;

}
