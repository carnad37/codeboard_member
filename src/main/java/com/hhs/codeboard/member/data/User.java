package com.hhs.codeboard.member.data;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class User {
    private String userSeq = "0";
    private String email;
    private String password;
    private String nickName;
    private String userType;
    private List<String> authList;
}
