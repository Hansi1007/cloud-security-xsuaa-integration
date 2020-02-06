package com.sap.cloud.security.samples;

import com.sap.cloud.security.xsuaa.XsuaaServiceConfiguration;
import com.sap.cloud.security.xsuaa.test.JwtGenerator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.util.Collections;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class TestControllerTest {

	private String jwtBob;
	private String jwtAlice;

	@Autowired
	private XsuaaServiceConfiguration xsuaaServiceConfiguration;

	@Before
	public void setUp() {
		jwtBob = new JwtGenerator(xsuaaServiceConfiguration.getClientId())
				.addCustomClaims(Collections.singletonMap("email", "Bob"))
				.getTokenForAuthorizationHeader();

		jwtAlice = new JwtGenerator(xsuaaServiceConfiguration.getClientId())
				.addCustomClaims(Collections.singletonMap("email", "Alice"))
				.getTokenForAuthorizationHeader();
	}

	@Autowired
	private MockMvc mockMvc;

	@Test
	public void readWithoutPermission_403() throws Exception {
		mockMvc.perform(get("/v1/method")
				.with(bearerToken(
						new JwtGenerator(xsuaaServiceConfiguration.getClientId()).getTokenForAuthorizationHeader())))
				.andExpect(status().isForbidden());
	}

	@Test
	public void readWith_Bob_403() throws Exception {
		mockMvc.perform(get("/v1/method")
				.with(bearerToken(jwtBob)))
				.andExpect(status().isForbidden());
	}

	@Test
	public void readWith_Alice_readAll_200() throws Exception {
		mockMvc.perform(get("/v1/method")
				.with(bearerToken(jwtAlice)))
				.andExpect(status().isOk());
	}

	private static class BearerTokenRequestPostProcessor implements RequestPostProcessor {
		private String token;

		public BearerTokenRequestPostProcessor(String token) {
			this.token = token;
		}

		@Override
		public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
			request.addHeader(HttpHeaders.AUTHORIZATION, this.token);
			return request;
		}
	}

	private static BearerTokenRequestPostProcessor bearerToken(String token) {
		return new BearerTokenRequestPostProcessor(token);
	}

}

