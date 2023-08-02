package com.likelionsns.final_project.config;

import com.likelionsns.final_project.service.UserService;
import com.likelionsns.final_project.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.StaticHeadersWriter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserService userService;
    @Value("${jwt.secret}")
    private String secretKey;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .httpBasic().disable()
                .csrf().disable()
                .cors().and()
//                .headers(headers -> headers.permissionsPolicy(policy -> policy.policy("browsing-topics=(self)")))
                .authorizeHttpRequests()
                .antMatchers("/index.jsp").permitAll()
                .antMatchers(HttpMethod.GET, "api/v1/posts/**").permitAll()
                .antMatchers(HttpMethod.GET, "api/v1/**").authenticated()
                .antMatchers(HttpMethod.POST, "/api/v1/users/join", "/api/v1/users/login").permitAll()
                .antMatchers(HttpMethod.POST, "/api/v1/**").authenticated()
                .antMatchers("/api/v1/users/{userId}/role").hasRole("ADMIN")
                .antMatchers(HttpMethod.PUT).authenticated()
                .antMatchers(HttpMethod.DELETE).authenticated()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilterBefore(new JwtFilter(userService, secretKey), UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
