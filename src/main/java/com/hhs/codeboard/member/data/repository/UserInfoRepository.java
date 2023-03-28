package com.hhs.codeboard.member.data.repository;

import com.hhs.codeboard.member.data.user.entity.UserInfoEntity;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Repository
public interface UserInfoRepository extends ReactiveCrudRepository<UserInfoEntity, Long> {

    Mono<UserInfoEntity> findByEmail(String email);


}
