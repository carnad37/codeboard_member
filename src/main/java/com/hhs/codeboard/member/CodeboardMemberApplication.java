package com.hhs.codeboard.member;

import com.hhs.codeboard.member.util.CommonUtil;
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

		if (!CommonUtil.checkPrdProfiles(args)) {
			BlockHound.install(builder -> {
				builder.allowBlockingCallsInside(HandshakeResponse.class.getName(), "writeConnectAttributes");
			});
		}

		SpringApplication.run(CodeboardMemberApplication.class, args);
	}

}
