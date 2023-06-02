package com.hhs.codeboard.member.web.service;

import com.hhs.codeboard.member.data.AuthDto;
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

    private final R2dbcEntityTemplate template;

    public Mono<AuthDto> loginUser(String email, String passwd) throws Exception {
        return userInfoRepo.findByEmail(email)
            .filter(entity -> entity != null && passwordEncoder.matches(passwd, entity.getPasswd()))
            .map(entity-> AuthDto.builder().email(entity.getEmail()).build())
            .switchIfEmpty(Mono.just(AuthDto.builder().message("이메일 또는 비밀번호가 틀렸습니다.").build()));
    }

    public Mono<UserInfoDto> selectUser(String email) throws Exception {
        // 1차 조회시 redis를 조회 (refresh token 만료전까지)
        // 조회결과가 없거나, 조회된 토큰이 만료되었을 수가 있음.
        // 만약 없을경우 DB조회
        return userInfoRepo.findByEmail(email)
                .map(entity -> toDTO(entity));
    }

    public Mono<UserInfoDto> saveUser(UserInfoRequest userInfoDto) {
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
        return userInfoRepo.findByEmail(email);
    }
}
