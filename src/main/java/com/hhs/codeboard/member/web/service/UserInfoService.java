package com.hhs.codeboard.member.web.service;

import com.hhs.codeboard.member.data.AuthDto;
import com.hhs.codeboard.member.data.repository.UserInfoRepository;
import com.hhs.codeboard.member.data.user.dto.UserInfoDto;
import com.hhs.codeboard.member.data.user.dto.request.UserInfoRequest;
import com.hhs.codeboard.member.data.user.entity.UserInfoEntity;
import com.hhs.codeboard.member.enumeration.ErrorCode;
import com.hhs.codeboard.member.expt.AppException;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.regex.Pattern;

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
            .map(entity-> AuthDto.builder()
                    .email(entity.getEmail())
                    .nickname(entity.getNickname())
                    .userSeq(entity.getUserSeq())
                    .build()
            )
            .switchIfEmpty(Mono.just(AuthDto.builder().message("이메일 또는 비밀번호가 틀렸습니다.").build()));
    }

    public Mono<UserInfoDto> selectUser(String email) throws Exception {
        // 1차 조회시 redis를 조회 (refresh token 만료전까지)
        // 조회결과가 없거나, 조회된 토큰이 만료되었을 수가 있음.
        // 만약 없을경우 DB조회
        return userInfoRepo.findByEmail(email)
                .map(this::toDTO);
    }

    public Mono<UserInfoDto> selectUserPublic(String email) throws Exception {
        // 1차 조회시 redis를 조회 (refresh token 만료전까지)
        // 조회결과가 없거나, 조회된 토큰이 만료되었을 수가 있음.
        // 만약 없을경우 DB조회
        return userInfoRepo.findByEmail(email)
                .map(this::toDTO);
    }

    private final Pattern emailCheck = Pattern.compile("^(([^<>()[\\\\]\\\\.,;:\\s@]+(\\.[^<>()[\\\\]\\\\.,;:\\s@]+)*))@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$");
    private final Pattern passCheck = Pattern.compile("(?!((?:[A-Za-z]+)|(?:[~!@#$%^&*()_+=]+)|(?:[0-9]+))$)[A-Za-z\\d~!@#$%^&*()_+=]{8,}$");
    private final Pattern nickNameCheck = Pattern.compile("[A-Za-z|\\d]*");

    public Mono<UserInfoDto> saveUser(UserInfoRequest userInfoDto) {
        UserInfoEntity entity = new UserInfoEntity();

        // member join validate
        if (StringUtils.isEmpty(userInfoDto.getEmail())
                || !emailCheck.matcher(userInfoDto.getEmail()).matches()) {
            return Mono.error(AppException.of(ErrorCode.JOIN_INCORRECT_EMAIL));
        } else if (StringUtils.isEmpty(userInfoDto.getPasswd())
                || !passCheck.matcher(userInfoDto.getPasswd()).matches()) {
            return Mono.error(AppException.of(ErrorCode.JOIN_INCORRECT_PASSWD));
        } else if (StringUtils.isEmpty(userInfoDto.getNickname())) {
//                && nickNameCheck.matcher(userInfoDto.getNickname()).matches()) {
            return Mono.error(AppException.of(ErrorCode.JOIN_INCORRECT_NICKNAME));
        }

        entity.setEmail(userInfoDto.getEmail());
        entity.setPasswd(passwordEncoder.encode(userInfoDto.getPasswd()));
        entity.setNickname(userInfoDto.getNickname());
//        entity.setUserType(userInfoDto.getUserType());
        // 유저타입은 고정
        entity.setUserType("N");
        entity.setRegDate(LocalDateTime.now());

        return userInfoRepo.save(entity)
                .map(tEntity->{
                    UserInfoDto infoDto = new UserInfoDto();
                    infoDto.setNickname(entity.getNickname());
                    return infoDto;
                })
                .onErrorResume(error->{
                    if (error instanceof DataIntegrityViolationException) {
                        // 아이디 중복 오류
                        return Mono.error(AppException.of(ErrorCode.DUPLICATE_ID));
                    } else {
                        // 그외의 오류
                        return Mono.error(AppException.of(ErrorCode.UNKNOWN));
                    }
                });
    }

    private UserInfoEntity toEntity(UserInfoDto userInfoDto) {
        return modelMapper.map(userInfoDto, UserInfoEntity.class);
    }

    private UserInfoDto toDTO(UserInfoEntity userInfoEntity) {
        UserInfoDto result = modelMapper.map(userInfoEntity, UserInfoDto.class);
        result.setPasswd(null);
        result.setRegDate(null);
        result.setModUser(null);
        result.setModDate(null);
        result.setDelDate(null);
        return result;
    }

    @Override
    public Mono<UserInfoEntity> login(String email, String passwd) {
        return userInfoRepo.findByEmail(email);
    }
}
