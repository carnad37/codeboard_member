package com.hhs.codeboard.member.util;

import lombok.experimental.UtilityClass;

import java.util.Arrays;
import java.util.Objects;

@UtilityClass
public class CommonUtil {

    public static boolean checkPrdProfiles(String[] args) {
        String profile = System.getProperty("spring.profiles.active");
        if ("prd".equals(profile)) return true;
        profile = Arrays.stream(args).filter(x-> !Objects.isNull(x)).map(x->x.split("spring.profiles.active=")).filter(x->x.length > 1 && "prd".equals(x[1])).map(x->x[1]).findFirst().orElse("local");
        return "prd".equals(profile);
    }

}
