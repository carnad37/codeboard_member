package com.hhs.codeboard.member.enumeration;

import com.hhs.codeboard.member.dto.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;

import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
public enum SecurityHeader {

//    ServerHttpRequest request = exchange.getRequest().mutate()
//                .header("cb_user_seq", "")
//                .header("cb_email", "")
//                .header("cb_user_type", "")
//                .header("cb_nickname", "")
//                .header("cb_auth", "")
//                .build();
    USER_SEQ("X-AUTH-USERSEQ")
    , EMAIL("X-AUTH-EMAIL")
    , USER_TYPE("X-AUTH-EMAIL")
    , NICKNAME("X-AUTH-NICKNAME")
    , ROLE("X-AUTH-ROLE");

    private final String key;

    public String getAsString(ServerHttpRequest request) {
        return request.getHeaders().getFirst(this.key);
    }

    public List<String> getAsList(ServerHttpRequest request) {
        return request.getHeaders().getOrDefault(this.key, Collections.emptyList());
    }

    public String getAsString(HttpHeaders header) {
        return header.getFirst(this.key);
    }

    public List<String> getAsList(HttpHeaders header) {
        return header.getOrDefault(this.key, Collections.emptyList());
    }

    public static User getUserFromHeader(ServerWebExchange request) {
        return getUserFromHeader(request.getRequest().getHeaders());
    }

    public static User getUserFromHeader(ServerHttpRequest request) {
        return getUserFromHeader(request.getHeaders());
    }

    public static User getUserFromHeader(HttpHeaders request) {
        User user = new User();
        for (SecurityHeader header : SecurityHeader.values()) {
            switch (header) {
                case USER_SEQ -> user.setUserSeq(header.getAsString(request));
                case USER_TYPE -> user.setUserType(header.getAsString(request));
                case EMAIL -> user.setEmail(header.getAsString(request));
                case NICKNAME -> user.setNickName(header.getAsString(request));
                case ROLE -> user.setAuthList(header.getAsList(request));
            }
        }
        return user;
    }
}
