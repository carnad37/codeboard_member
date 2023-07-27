package com.hhs.codeboard.member;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//@SpringBootApplication(exclude = {R2dbcAutoConfiguration.class})
@SpringBootApplication
public class CodeboardMemberApplication {

	public static void main(String[] args) {
		SpringApplication.run(CodeboardMemberApplication.class, args);
	}

}
