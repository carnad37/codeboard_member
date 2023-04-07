package com.hhs.codeboard.member;

import com.hhs.codeboard.member.data.repository.UserInfoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.AbstractClientHttpRequest;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@AutoConfigureMockMvc
@SpringBootTest
class CodeboardMemberApplicationTests {

//	@Autowired
//	private MockMvc mockMvc;

	@Autowired
	private UserInfoRepository userInfoRepository;

	@Test
	void connectTest() {
		userInfoRepository.findById((long)1);
	}



//	@Test
//	void loginTest() throws Exception{
//
//		this.mockMvc.perform(
//				post("/gw/login")
//						.param("email", "test@test.co.kr")
//						.param("password", "password")
//		).andExpect(status().isOk()).andDo(print());
//
//	}

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
