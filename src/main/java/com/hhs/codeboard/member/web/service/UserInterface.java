package com.hhs.codeboard.member.web.service;

import com.hhs.codeboard.member.data.user.entity.UserInfoEntity;
import reactor.core.publisher.Mono;

public interface UserInterface {

    Mono<UserInfoEntity> login(String id, String passwd);

}
