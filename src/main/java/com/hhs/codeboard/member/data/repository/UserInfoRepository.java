package com.hhs.codeboard.member.data.repository;

import com.hhs.codeboard.member.data.user.entity.UserInfoEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserInfoRepository extends ReactiveCrudRepository<UserInfoEntity, Long> {


}
