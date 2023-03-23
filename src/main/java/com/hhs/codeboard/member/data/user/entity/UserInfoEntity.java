package com.hhs.codeboard.member.data.user.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("user_info")
public class UserInfoEntity {

    @Id
    private Long seq;

    private String nickname;

    private String passwd;

    private String userType;

    private int modUserSeq;

    private LocalDateTime regDate;

    private LocalDateTime modDate;

    private LocalDateTime delDate;

}
