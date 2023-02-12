package com.example.auth.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;

import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;



@Configuration
@Slf4j
@EnableWebSecurity(debug = true)
@EnableMethodSecurity
public class SecurityConfig  {

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

    http.csrf().disable()
      .authorizeHttpRequests(auth -> auth.anyRequest().access(new CustomAuthorizationManager()))
      .httpBasic();


    return http.build();
  }

  @Bean
  public DaoAuthenticationProvider authenticationProvider(CustomUserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
    DaoAuthenticationProvider auth = new DaoAuthenticationProvider();
    auth.setUserDetailsService(userDetailsService);
    auth.setPasswordEncoder(passwordEncoder);
    return auth;
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return NoOpPasswordEncoder.getInstance();
  }

}
