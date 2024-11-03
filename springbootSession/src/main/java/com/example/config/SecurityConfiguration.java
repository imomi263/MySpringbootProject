package com.example.config;

import com.alibaba.fastjson2.JSONObject;
import com.example.entity.RestBean;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.SecurityFilterChain;

import java.io.IOException;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(conf ->
                        conf.requestMatchers("/api/auth/**","/error").permitAll()
                                .anyRequest().authenticated()
                )
                .formLogin(
                        formLogin -> formLogin.loginProcessingUrl("/api/auth/login")
                                .successHandler(this::onAuthSuccess)

                )
                .logout(
                        logout -> logout.logoutUrl("/api/auth/logout")
                )
                .csrf(
                        AbstractHttpConfigurer::disable
                )
                .exceptionHandling(conf->conf
                        .authenticationEntryPoint(this::onUnauthorized)
                        .accessDeniedHandler(this::onAccessDeny)

                )
                .build();



    }


    private void onAuthSuccess(HttpServletRequest request,
                          HttpServletResponse response,
                          Authentication auth ) throws IOException {
        response.setCharacterEncoding("UTF-8");

        response.getWriter().write(JSONObject.toJSONString(RestBean.success("login success")));
    }

    public void onUnauthorized(HttpServletRequest request,
                               HttpServletResponse response,
                               AuthenticationException auth )throws IOException, ServletException {
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(JSONObject.toJSONString(RestBean.failure(401,auth.getMessage())));
    }

    public void onAccessDeny(HttpServletRequest request,
                             HttpServletResponse response,
                             AccessDeniedException auth) throws IOException, ServletException {
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(JSONObject.toJSONString(RestBean.failure(403,auth.getMessage())));
    }
}
