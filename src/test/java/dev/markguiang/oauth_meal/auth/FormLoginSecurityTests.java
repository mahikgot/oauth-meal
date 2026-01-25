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

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@Import(TestSecurityConfig.class)
public class FormLoginSecurityTests {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mvc;

    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    public void shouldAuthenticate_whenValidFormLoginCredentialsIncluded() throws Exception {
        mvc.perform(post("/auth/login")
                        .formField("username", TestSecurityConfig.validUsername)
                        .formField("password", TestSecurityConfig.validRawPassword)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(authenticated().withUsername(TestSecurityConfig.validUsername));
    }

    @Test
    public void shouldNotAuthenticate_whenInvalidFormLoginCredentialsIncluded() throws Exception {
        mvc.perform(post("/auth/login")
                        .formField("username", TestSecurityConfig.invalidUsername)
                        .formField("password", TestSecurityConfig.invalidRawPassword)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/auth/login?error"));
    }
}
