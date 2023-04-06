package com.hhs.codeboard.member.web.service;

import com.hhs.codeboard.member.auth.TokenAuthService;
import com.hhs.codeboard.member.data.repository.UserInfoRepository;
import com.hhs.codeboard.member.data.user.dto.UserInfoDto;
import com.hhs.codeboard.member.data.user.dto.request.UserInfoRequest;
import com.hhs.codeboard.member.data.user.entity.UserInfoEntity;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserInfoService implements UserInterface{

    private final UserInfoRepository userInfoRepo;

    private final ModelMapper modelMapper;

    private final BCryptPasswordEncoder passwordEncoder;

    private final TokenAuthService tokenAuthService;

    private final R2dbcEntityTemplate template;

    public Mono<UserInfoDto> selectUser(String email, String passwd) throws Exception {
        // 1차 조회시 redis를 조회 (refresh token 만료전까지)
        // 조회결과가 없거나, 조회된 토큰이 만료되었을 수가 있음.
        // 만약 없을경우 DB조회
        return userInfoRepo.findByEmail(email)
            .mapNotNull(entity->{
                if (passwordEncoder.matches(passwd, entity.getPasswd())) {
                    return this.toDTO(entity);
                } else {
                    return new UserInfoDto();
                }
            });
    }

//    public Mono<UserInfoDto> selectUserByToken(String token) throws Exception {
//        // 1차 조회시 redis를 조회 (refresh token 만료전까지)
//        // TODO :: Redis get UserInfo by token
//        // 조회결과가 없거나, 조회된 토큰이 만료되었을 수가 있음.
//        // 만약 없을경우 DB조회
//        // TODO :: DB get UserInfo by token
//        tokenAuthService.authorized();?
//    }

    public Mono<UserInfoDto> saveUser(UserInfoRequest userInfoDto, String editUser) {
        // 유저정보가 업데이트 되는경우 반드시 패스워드 비교필요.
        // 해당기능은 무조건 private로 진행되어야함.
        UserInfoEntity entity = new UserInfoEntity();
        entity.setEmail(userInfoDto.getEmail());
        entity.setPasswd(passwordEncoder.encode(userInfoDto.getPasswd()));
        entity.setNickname(userInfoDto.getNickname());
        entity.setUserType(userInfoDto.getUserType());
        entity.setRegDate(LocalDateTime.now());

        return userInfoRepo.save(entity)
                .map(this::toDTO);
    }

    private UserInfoEntity toEntity(UserInfoDto userInfoDto) {
        return modelMapper.map(userInfoDto, UserInfoEntity.class);
    }

    private UserInfoDto toDTO(UserInfoEntity userInfoEntity) {
        return modelMapper.map(userInfoEntity, UserInfoDto.class);
    }

    @Override
    public Mono<UserInfoEntity> login(String email, String passwd) {
        return userInfoRepo.findByEmail(email);;
    }
}
