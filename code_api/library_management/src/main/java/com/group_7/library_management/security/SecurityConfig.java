package com.group_7.library_management.security;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            BearerTokenAuthenticationFilter bearerTokenAuthenticationFilter
    ) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(
                                "/api/v1/auth/register/email/code",
                                "/api/v1/auth/register/email/resend",
                                "/api/v1/auth/register/email/verify",
                                "/api/v1/auth/register/phone/code",
                                "/api/v1/auth/register/phone/resend",
                                "/api/v1/auth/register/phone/verify",
                                "/api/v1/auth/password/forgot/code",
                                "/api/v1/auth/password/code/resend",
                                "/api/v1/auth/password/code/verify",
                                "/api/v1/auth/password/reset",
                                "/api/v1/auth/login",
                                "/api/v1/auth/biometric/login",
                                "/api/v1/payments/sepay/webhook",
                                "/uploads/book-covers/**"
                        ).permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/books/*/reviews/mine").authenticated()
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/v1/books",
                                "/api/v1/books/**",
                                "/api/v1/authors",
                                "/api/v1/authors/**",
                                "/api/v1/publishers",
                                "/api/v1/publishers/**",
                                "/api/v1/categories",
                                "/api/v1/categories/**",
                                "/api/v1/notifications"
                        ).permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/books/*/reviews").authenticated()
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/books/*/reviews/*").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/books/*/reviews/*").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/v1/books", "/api/v1/books/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/books/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/books/**").hasRole("ADMIN")
                        .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated())
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint((request, response, exception) ->
                                response.sendError(HttpServletResponse.SC_UNAUTHORIZED))
                        .accessDeniedHandler((request, response, exception) ->
                                response.sendError(HttpServletResponse.SC_FORBIDDEN)))
                .addFilterBefore(
                        bearerTokenAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                )
                .httpBasic(httpBasic -> httpBasic.disable())
                .formLogin(formLogin -> formLogin.disable())
                .build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource(
            @Value("${app.cors.allowed-origins:http://localhost:3000,http://127.0.0.1:3000}")
            List<String> allowedOrigins
    ) {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(allowedOrigins);
        configuration.setAllowedMethods(List.of(
                "GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"
        ));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);
        return source;
    }
}
