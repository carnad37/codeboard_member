package com.hhs.codeboard.member;

import org.mariadb.r2dbc.message.client.HandshakeResponse;
import org.mariadb.r2dbc.message.flow.AuthenticationFlow;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.blockhound.BlockHound;
import reactor.blockhound.integration.BlockHoundIntegration;
import reactor.blockhound.integration.ReactorIntegration;
import reactor.blockhound.integration.RxJava2Integration;
import reactor.blockhound.integration.StandardOutputIntegration;

import java.io.FilterInputStream;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.Objects;
import java.util.ResourceBundle;

//@SpringBootApplication(exclude = {R2dbcAutoConfiguration.class})
@SpringBootApplication
public class CodeboardMemberApplication {

	public static void main(String[] args) {
		boolean result = Arrays.stream(args).filter((x)->!Objects.isNull(x)).map(x->x.split("spring.profiles.activ=")).anyMatch(x->x.length > 1 && !"prd".equals(x[1]));
		if (!"prd".equals(System.getProperty("spring.profiles.active")) || result) {
			BlockHound.install(builder -> {
				builder.allowBlockingCallsInside(HandshakeResponse.class.getName(), "writeConnectAttributes");
			});
		}

		SpringApplication.run(CodeboardMemberApplication.class, args);
	}

}
