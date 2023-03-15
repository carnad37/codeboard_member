package com.hhs.codeboard.member;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.AbstractClientHttpRequest;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.test.web.servlet.MockMvc;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
class CodeboardMemberApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void loginTest() throws Exception{

		this.mockMvc.perform(
				post("/gw/login")
						.param("email", "test@test.co.kr")
						.param("password", "password")
		).andExpect(status().isOk()).andDo(print());

	}

	@Test
	void reactiveJavaTest() throws Exception {

		Mono<String> mono = Mono.just("1");
		List<String> test = new ArrayList<>();
		mono.subscribe(iMono->{
			Mono<String> iiMono = Mono.just("2");
			iiMono.subscribe(iiiMono->{
				test.add(iiiMono);
			});
			test.add(iMono);
		});
		test.stream().forEach(System.out::println);
	}


	@Test
	void testMutate() {
		ClientHttpRequest request = new AbstractClientHttpRequest() {
			@Override
			protected OutputStream getBodyInternal(HttpHeaders headers) throws IOException {
				return null;
			}

			@Override
			protected ClientHttpResponse executeInternal(HttpHeaders headers) throws IOException {
				return null;
			}

			@Override
			public HttpMethod getMethod() {
				return null;
			}

			@Override
			public URI getURI() {
				return null;
			}
		};

		request.getHeaders().remove("1");
	}

}
