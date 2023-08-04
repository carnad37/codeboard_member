package com.hhs.codeboard.member.web.service;

import reactor.core.publisher.Mono;

public interface RecaptchaService {

    Mono<Boolean> checkRecaptcha(String token);

}
