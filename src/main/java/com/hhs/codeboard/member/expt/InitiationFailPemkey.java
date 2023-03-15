package com.hhs.codeboard.member.expt;

/**
 * JWT에서 pemkey를 로드하는데 실패한경우
 */
public class InitiationFailPemkey extends RuntimeException {

    public InitiationFailPemkey(String message) {
        super(message);
    }
}
