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

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;

@Configuration
public class SecurityConfig {
    @Bean
    public SecurityFilterChain configure(HttpSecurity http, FilterRegistrationBean<BasicAuthenticationFilter> baf) {
        http.authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers("/auth/login", "/error")
                        .permitAll()
                        .anyRequest()
                        .authenticated())
                .addFilter(baf.getFilter())
                .formLogin((formLogin) -> formLogin.loginPage("/auth/login"))
                .oauth2Login((oauth2) -> oauth2.loginPage("/auth/login"));
        return http.build();
    }

    @Bean
    public FilterRegistrationBean<BasicAuthenticationFilter> basicAuthenticationFilter(
            AuthenticationManager am, SecurityContextRepository hsscp) {
        var baf = new BasicAuthenticationFilter(am);
        baf.setSecurityContextRepository(hsscp);
        var frb = new FilterRegistrationBean<>(baf);
        frb.setEnabled(false);
        return frb;
    }

    @Bean
    SecurityContextRepository securityContextRepository() {
        return new HttpSessionSecurityContextRepository();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider(UserDetailsService uds, PasswordEncoder pe) {
        var provider = new DaoAuthenticationProvider(uds);
        provider.setPasswordEncoder(pe);
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(DaoAuthenticationProvider dap) {
        return new ProviderManager(dap);
    }
}
