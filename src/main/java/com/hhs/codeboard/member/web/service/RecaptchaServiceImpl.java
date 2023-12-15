package com.hhs.codeboard.member.web.service;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
public class RecaptchaServiceImpl implements RecaptchaService{

    @Autowired
    @Qualifier(value = "recaptchaClient")
    private WebClient recaptchaClient;

    @Value("${codeboard.recaptcha.key}")
    private String recaptchaKey;

    @Override
    public Mono<Boolean> checkRecaptcha(String token) {
        return recaptchaClient
                .post()
                .accept(MediaType.APPLICATION_JSON)
                .body(
                        BodyInserters
                                .fromFormData("secret", recaptchaKey)
                                .with("response", token)
//                                .with("remoteip", "test") // request ip info
                )
                .retrieve()
                .bodyToMono(MAP_TYPE_REF)
                .map(target->{
                    Boolean result = (Boolean) target.get("success");
                    return result;
                });
//                .bodyToMono(RecaptchaResponse.class)
//                .map(RecaptchaResponse::getSuccess);
    }

    // MAP으로 처리용
    private static final ParameterizedTypeReference<Map<String, Object>> MAP_TYPE_REF = new ParameterizedTypeReference<>() {};
//    @Getter
//    @Setter
//    private static class RecaptchaResponse {
//        private Boolean success;
//    }


}
