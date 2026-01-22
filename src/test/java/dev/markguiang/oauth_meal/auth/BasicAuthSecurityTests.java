/*
	(c) Copyright 2026 Mark Guiang. All rights reserved.

	Licensed under the Apache License, Version 2.0 (the "License"); you may not
	use this file except in compliance with the License. You may obtain a copy of
	the License at

	 http://www.apache.org/licenses/LICENSE-2.0

	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
	WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
	License for the specific language governing permissions and limitations under
	the License.
*/
package dev.markguiang.oauth_meal.auth;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@Import(TestSecurityConfig.class)
public class BasicAuthSecurityTests {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mvc;
    private RequestPostProcessor validBasic =
            httpBasic(TestSecurityConfig.validUsername, TestSecurityConfig.validRawPassword);
    private RequestPostProcessor invalidBasic = httpBasic("NOTEXISTING", "WRONGPASSWORD");

    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    public void shouldAllowAccess_whenValidBasicAuthProvided() throws Exception {
        mvc.perform(get("/auth/test").with(validBasic)).andExpect(status().isOk());
    }

    @Test
    public void shouldAllowAccess_whenSessionIsReusedWithoutCredentials() throws Exception {
        var session = new MockHttpSession();
        mvc.perform(get("/auth/test").with(validBasic).session(session)).andExpect(status().isOk());

        mvc.perform(get("/auth/test").session(session)).andExpect(status().isOk());
    }

    @Test
    public void shouldRedirectToLogin_whenInvalidBasicAuthProvided() throws Exception {
        mvc.perform(get("/auth/test").with(invalidBasic))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/login"));
    }
}
