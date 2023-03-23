package com.hhs.codeboard.member.web.service;

import com.hhs.codeboard.member.data.repository.UserInfoRepository;
import com.hhs.codeboard.member.data.user.dto.UserInfoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@DependsOn("userInfoRepository")
public class UserInfoService {

    @Autowired
    private UserInfoRepository userInfoRepo;

    public Mono<UserInfoDto> selectUser(long userSeq) {
        return userInfoRepo.findById(userSeq)
                .map(entity->{
                    return new UserInfoDto();
                });
    }


}
