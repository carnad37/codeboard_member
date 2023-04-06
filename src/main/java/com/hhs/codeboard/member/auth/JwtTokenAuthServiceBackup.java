//package com.hhs.codeboard.member.auth;
//
//import com.auth0.jwt.JWT;
//import com.auth0.jwt.JWTVerifier;
//import com.auth0.jwt.algorithms.Algorithm;
//import com.auth0.jwt.exceptions.JWTVerificationException;
//import com.auth0.jwt.interfaces.DecodedJWT;
//import com.google.common.collect.ImmutableMap;
//import com.hhs.codeboard.member.data.AuthDto;
//import com.hhs.codeboard.member.data.User;
//import com.hhs.codeboard.member.data.user.dto.UserInfoDto;
//import org.springframework.data.redis.core.ReactiveRedisOperations;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseCookie;
//import org.springframework.http.server.reactive.ServerHttpRequest;
//import org.springframework.http.server.reactive.ServerHttpResponse;
//import org.springframework.util.MultiValueMap;
//import org.springframework.util.StringUtils;
//import org.springframework.web.reactive.function.client.WebClient;
//import org.springframework.web.server.ServerWebExchange;
//import reactor.core.publisher.Mono;
//
//import java.security.KeyPair;
//import java.security.KeyPairGenerator;
//import java.security.NoSuchAlgorithmException;
//import java.security.interfaces.RSAPrivateKey;
//import java.security.interfaces.RSAPublicKey;
//import java.time.Duration;
//import java.time.Instant;
//import java.time.ZoneId;
//import java.time.temporal.ChronoUnit;
//import java.util.Map;
//import java.util.Optional;
//
///**
// * 동적으로 생성되므로 따로 Bean으로 등록하지 않는다.
// */
//public class JwtTokenAuthServiceBackup implements TokenAuthService {
//
//    private Algorithm algorithm;
//    private AlgorithmSupporter.AlgorithmType algorithmType = AlgorithmSupporter.AlgorithmType.RS;
//    private AlgorithmSupporter.AlgorithmKeySize algorithmKeySize = AlgorithmSupporter.AlgorithmKeySize._384;
//    private ReactiveRedisOperations<String, User> redisUserOperation;
//    private WebClient userClient;
//    private Map<String, Object> header;
//    private JWTVerifier verifier;
//    private ZoneId zoneId = ZoneId.of("Asia/Seoul");
//
//    private String accessTokenName = "CB_AT";
//    private String refreshTokenName = "CB_RT";
//
//    /**
//     * 코드상에서 key 생성
//     * 기존 키값들은 만료되는 문제가 있다.
//     * @param algorithmDto
//     * @throws NoSuchAlgorithmException
//     */
//    public JwtTokenAuthServiceBackup(AlgorithmSupporter.AlgorithmDto algorithmDto, WebClient userClient, ReactiveRedisOperations<String, User> redisOperations) throws NoSuchAlgorithmException {
//        if (algorithmDto.getSize() != null && algorithmDto.getType() != null) {
//            // 기존키 사용
//            this.algorithmType = algorithmDto.getType();
//            this.algorithmKeySize = algorithmDto.getSize();
//
//            this.algorithm = algorithmType.getAlgorithm(algorithmDto.getSize(), algorithmDto);
//        } else if (algorithmDto.getSize() != null || algorithmDto.getType() != null) {
//            // 수동키 생성
//            this.algorithmType = algorithmDto.getType() == null ? this.algorithmType : algorithmDto.getType();
//            this.algorithmKeySize = algorithmDto.getSize() == null ? this.algorithmKeySize : algorithmDto.getSize();
//
//            KeyPairGenerator kpg = KeyPairGenerator.getInstance(this.algorithmType.getInstanceType());
//            kpg.initialize(this.algorithmKeySize.getSize());
//            KeyPair kp = kpg.generateKeyPair();
//
//            this.algorithm = Algorithm.RSA256((RSAPublicKey) kp.getPublic(), (RSAPrivateKey) kp.getPrivate());
//        }
//        if (this.algorithm == null) throw new NoSuchAlgorithmException("not found algorithm");
//
//        // user 정보 획득용 redis operation 세팅 및 webclient 초기화
//        this.redisUserOperation = redisUserOperation;
//        this.userClient = userClient;
//
//        // 헤더 초기화
//        this.header = ImmutableMap.<String, Object>builder()
//                .put(JWTheader.typ.name(), "JWT")
//                .put(JWTheader.alg.name(), this.algorithmType.name() + this.algorithmKeySize.getSize())
//                .build();
//
//        // 토큰 유효성 체커
//        this.verifier = JWT.require(algorithm)
////                .withIssuer("codeboard")
////                .acceptIssuedAt(TimeUnit.MINUTES.toMillis(30))
//                .build();
//    }
//
//    public String getToken(User user) {
//        // 토큰 생성
//        return JWT.create().withHeader(this.header)
//                .withIssuer("codeboard")
//                .withSubject("access_token")
//                .withAudience(user.getEmail())
//                .withIssuedAt(Instant.now().atZone(this.zoneId).toInstant())
//                .sign(algorithm);
//    }
//
////    @Override
////    public Mono<String> getRefreshToken(User user) throws NoSuchAlgorithmException {
////        MessageDigest md = MessageDigest.getInstance("SHA-256");
////        return Mono.just(
////                String.format("%064x", new BigInteger(
////                        md.digest(UUID.randomUUID().toString().replaceAll("-","").getBytes()))
////                )
////        );
////    }
//
//    @Override
//    public void login(ServerWebExchange exchange) {
//        ServerHttpRequest request = exchange.getRequest();
//        ServerHttpResponse response = exchange.getResponse();
//
//        MultiValueMap<String, String> paramMap = request.getQueryParams();
//
//        //토큰 체크
//        Optional<String> accessToken = Optional.ofNullable(request.getCookies())
//                .map(cookieMap -> cookieMap.getFirst(this.accessTokenName))
//                .map(cookie -> cookie.getValue());
//
//        if (!accessToken.isPresent()) {
//            User user = new User();
//            user.setEmail(paramMap.get("email").get(0));
//            user.setEmail(paramMap.get("passwd").get(0));
//
//            // TODO :: 오류 가능성
//            //  현재 로그인 중인경우 (토큰값이 포함) -> 토큰인증 시행. 문제가 있을경우 재로그인 진행.
//            //  로그인에 실패했을경우 -> 403 에러
//
//            // 이메일 & 비밀번호
//            // 리프레시 토큰 + 유저정보 받음
//            Mono<AuthDto> authMono = this.findUserInfo(user.getEmail(), user.getPassword());
//            authMono.subscribe(mono->{
//                mono.setAccessToken(this.getToken(mono.getUser()));
//                //로그인 진행
//                this.setUserInfo(mono, request, response);
//            });
//
//        } else {
//            // 토큰 등록
//            this.tokenProcess(accessToken.get(), request, response);
//        }
//    }
//
//
//    @Override
//    public void authorization(ServerWebExchange exchange) {
//        // 토큰 파싱을 통해 유저정보 획득
//        ServerHttpRequest request = exchange.getRequest();
//        ServerHttpResponse response = exchange.getResponse();
//        //TODO :: 각각의 오류별로 처리할 필요가있음.
//
//        //무조건 헤더의 auth정보 초기화
//        exchange = this.clearUserInfoInHeader(exchange);
//
//        // 인증에 필요한 토큰정보가 없을경우(has no cookie or has no access token)
//        // => 인증없이 진행함.
//        Optional.ofNullable(request.getCookies())
//                .map(cookies -> cookies.getFirst("CB_AT"))
//                .filter((accessTokenCookie)->StringUtils.hasText(accessTokenCookie.getValue()))
//                .ifPresent((accessTokenCookie)->this.tokenProcess(accessTokenCookie.getValue(), request, response));
//    }
//
//    @Override
//    public UserInfoDto authentication(String accessToken, String refreshToken) {
//        DecodedJWT decodedJWT;
//        try {
//            decodedJWT = verifier.verify(accessToken);
//            if (Instant.now().isAfter(decodedJWT.getIssuedAtAsInstant().plus(30, ChronoUnit.MINUTES))) {
//                // 만료가 된 토큰일 경우
//                // => 리프래시 토큰을 사용해서 다시 accessToken을 발급한다.
//                Mono<AuthDto> authMono = this.getUserAndRefreshToken(this.getUserSeq(decodedJWT), refreshToken);
//                authMono.subscribe((mono)-> {
//                    mono.setAccessToken(this.getToken(mono.getUser()));
//                    // 로그인 진행
//                    this.setUserInfo(mono, request, response);
//                });
//            } else {
//                // 유효한 토큰일경우
//                Mono<User> userMono = redisUserOperation.opsForValue().get(this.getUserSeq(decodedJWT));
//                userMono.subscribe(mono->this.setUserInfoInHeader(mono, request));
//            }
//        } catch (JWTVerificationException ne) {
//            // 인증 오류가 있는 토큰일 경우
//            // => 403오류로 에러페이지로 전송시킴
//            response.setStatusCode(HttpStatus.FORBIDDEN);
//        }
//
//    }
//
//    /**
//     * accessToken이 있는경우의 로직
//     * GW 기준의 로직
//     * 헤더에다가 인증정보를 담아서 보낸다.
//     *
//     * @param accessToken
//     * @param request
//     * @param response
//     */
//    private void tokenProcess(String accessToken,  ServerHttpRequest request,  ServerHttpResponse response) {
//        DecodedJWT decodedJWT;
//        try {
//            decodedJWT = verifier.verify(accessToken);
//            if (Instant.now().isAfter(decodedJWT.getIssuedAtAsInstant().plus(30, ChronoUnit.MINUTES))) {
//                // 만료가 된 토큰일 경우
//                // => 리프래시 토큰을 사용해서 다시 accessToken을 발급한다.
//                String refreshToken = Optional.ofNullable(request.getCookies())
//                        .map(cookies -> cookies.getFirst(refreshTokenName))
//                        .orElseThrow()
//                        .getValue();
//
//                Mono<AuthDto> authMono = this.getUserAndRefreshToken(this.getUserSeq(decodedJWT), refreshToken);
//                authMono.subscribe((mono)-> {
//                    mono.setAccessToken(this.getToken(mono.getUser()));
//                    // 로그인 진행
//                    this.setUserInfo(mono, request, response);
//                });
//            } else {
//                // 유효한 토큰일경우
//                Mono<User> userMono = redisUserOperation.opsForValue().get(this.getUserSeq(decodedJWT));
//                userMono.subscribe(mono->this.setUserInfoInHeader(mono, request));
//            }
//        } catch (JWTVerificationException ne) {
//            // 인증 오류가 있는 토큰일 경우
//            // => 403오류로 에러페이지로 전송시킴
//            response.setStatusCode(HttpStatus.FORBIDDEN);
//        }
//    }
//
//    private void setUserInfo (AuthDto auth, ServerHttpRequest request, ServerHttpResponse response) {
//        // 유저 정보 레디스 저장
//        this.redisUserOperation.opsForValue().set(auth.getUser().getUserSeq(), auth.getUser());
////        this.setUserInfoInHeader(auth.getUser(), request.getHeaders());
//        // 토큰 쿠키 생성
//        this.bakeTokenCookie(auth.getAccessToken(), auth.getRefreshToken(), response);
//    }
//
//    protected enum JWTheader {
//        typ, alg;
//    }
//
//    /**
//     * Member module에서 유저정보 획득
//     * @param userKey
//     * @param password
//     * @return
//     */
//    private Mono<AuthDto> findUserInfo(String userKey, String password) {
//        AuthDto auth = new AuthDto();
//
//        User user = new User();
//        user.setEmail(userKey);
//        user.setPassword(password);
//
//        auth.setUser(user);
//
//        // 회원정보와 refresh토큰을 담아서 응답받음
//        return this.userClient.post()
//                .uri("/gw/login")
//                .body(auth, AuthDto.class)
//                .retrieve()
//                .bodyToMono(AuthDto.class);
//    }
//
//    /**
//     * Member module에서 유저정보 획득
//     * @param userSeq
//     * @param refreshToken
//     * @return
//     */
//    private Mono<AuthDto> getUserAndRefreshToken(String userSeq, String refreshToken) {
//        AuthDto auth = new AuthDto();
//
//        User user = new User();
//        user.setUserSeq(userSeq);
//
//        auth.setUser(user);
//        auth.setRefreshToken(refreshToken);
//
//        return this.userClient.post()
//                .uri("/gw/refresh")
//                .body(auth, AuthDto.class)
//                .retrieve()
//                .bodyToMono(AuthDto.class);
//    }
//
//    /**
//     * 액세스 토큰 만료시 refresh 및 유저정보를 함께담아서 확인.
//     * @param refreshToken
//     * @param accessToken
//     * @return
//     */
//    private Mono<AuthDto> findUserInfoByUserRefreshToken(String userSeq, String accessToken, String refreshToken) {
//        AuthDto auth = new AuthDto();
//
//        User user = new User();
//        user.setUserSeq(userSeq);
//
//        auth.setUser(user);
//        auth.setAccessToken(accessToken);
//        auth.setRefreshToken(refreshToken);
//        return this.userClient.post()
//                .uri("/gw/refresh")
//                .body(auth, AuthDto.class)
//                .retrieve()
//                .bodyToMono(AuthDto.class);
//    }
//
//    private String getUserSeq(DecodedJWT decodedJWT) {
//        return decodedJWT.getAudience().get(0);
//    }
//
//    // 토큰쿠키를 굽자!
//    private void bakeTokenCookie(String accessToken, String refreshToken, ServerHttpResponse response) {
//        if (StringUtils.hasText(accessToken)) {
//            ResponseCookie accessTokenCookie = this.makeCookie(this.accessTokenName, accessToken);
//            response.addCookie(accessTokenCookie);
//        }
//        if (StringUtils.hasText(refreshToken)) {
//            ResponseCookie refreshTokenCookie = this.makeCookie(this.refreshTokenName, refreshToken);
//            response.addCookie(refreshTokenCookie);
//        }
//    }
//
//    /**
//     * 쿠키 생성
//     * TODO :: 쿠키 생성 기본옵션 설정가능하게 수정??
//     * @param name
//     * @param value
//     * @return
//     */
//    private ResponseCookie makeCookie(String name, String value) {
//        return ResponseCookie.from(name, value)
//                .httpOnly(true)
//                .maxAge(Duration.ofMinutes(30))
////                    .secure(true)
//                .sameSite("Strict")
//                .path("/")
//                .build();
//    }
//
//    /**
//     * 모듈로 가는 요청 헤더에 인증정보 심기
//     * 인증정보가 없는경우 같은 이름의 헤더 덮어써버리기
//     */
//    private void setUserInfoInHeader(User user, ServerHttpRequest request) {
////        HttpHeaders headers = request.getHeaders().remove();
//        request.mutate()
//                .header("cb_user_seq", user.getUserSeq())
//                .header("cb_email", user.getEmail())
//                .header("cb_user_type", user.getUserType())
//                .header("cb_nickname", user.getNickName())
//                .header("cb_auth", user.getAuthList().stream().toArray(String[]::new));
//    }
//    /**
//     * 모듈로 가는 요청 헤더에 인증정보 심기
//     * 인증정보가 없는경우 같은 이름의 헤더 덮어써버리기
//     */
//    private ServerWebExchange clearUserInfoInHeader(ServerWebExchange exchange) {
//        ServerHttpRequest request = exchange.getRequest().mutate()
//                .header("cb_user_seq", "")
//                .header("cb_email", "")
//                .header("cb_user_type", "")
//                .header("cb_nickname", "")
//                .header("cb_auth", "")
//                .build();
//        return exchange.mutate().request(request).build();
//    }
//}
