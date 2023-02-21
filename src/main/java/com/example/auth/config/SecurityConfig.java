package com.example.auth.config;

import com.example.auth.auth.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.expression.DefaultHttpSecurityExpressionHandler;
import org.springframework.security.web.access.expression.WebExpressionAuthorizationManager;


@Configuration
@Slf4j
@EnableWebSecurity(debug = true)
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final ApplicationContext applicationContext;

    /**
     * WebExpression Authorization Manager uses  DefaultHttpSecurityExpressionHandler as its SpEL expression handler,
     * which is not aware of the application context. A way is to set the application context of the expression handler
     *
     * @param expression Spel Expression
     * @return WebExpressionAuthorizationManager with set application context
     */

    private WebExpressionAuthorizationManager webExpressionAuthorizationManager(String expression) {
        final var expressionHandler = new DefaultHttpSecurityExpressionHandler();
        expressionHandler.setApplicationContext(applicationContext);
        final var authorizationManager = new WebExpressionAuthorizationManager(expression);
        authorizationManager.setExpressionHandler(expressionHandler);
        return authorizationManager;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.csrf().disable()
                .authorizeHttpRequests(auth ->
                        auth.requestMatchers(HttpMethod.GET, "/api/v1/career/profile/{profileId}/history/{id}")
                                .access(webExpressionAuthorizationManager("@filterAuthorizationChecker.hasPermissionToActOnResource(authentication, \"READ\", #profileId, \"CAREER_HISTORY\", #id)"))
                                .requestMatchers(HttpMethod.PUT, "/api/v1/career/profile/{profileId}/history/{id}")
                                .access(webExpressionAuthorizationManager("@filterAuthorizationChecker.hasPermissionToActOnResource(authentication, \"WRITE\", #profileId, \"CAREER_HISTORY\", #id)"))
                                .anyRequest().permitAll()
                )
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
