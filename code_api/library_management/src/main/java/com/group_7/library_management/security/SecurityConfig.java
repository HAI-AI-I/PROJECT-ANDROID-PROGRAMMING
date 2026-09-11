        package com.group_7.library_management.security;

        import org.springframework.context.annotation.Bean;
        import org.springframework.context.annotation.Configuration;
        import org.springframework.security.config.annotation.web.builders.HttpSecurity;
        import org.springframework.security.config.http.SessionCreationPolicy;
        import org.springframework.security.web.SecurityFilterChain;

        @Configuration
        public class SecurityConfig {

        @Bean
        SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                return http
                        .csrf(csrf -> csrf.disable())
                        .sessionManagement(session ->
                                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                        .authorizeHttpRequests(authorize -> authorize
                                .requestMatchers("/api/v1/auth/register").permitAll()
                                // .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                                .requestMatchers("/api/v1/admin/**").permitAll()
                                .anyRequest().authenticated())
                        .httpBasic(httpBasic -> httpBasic.disable())
                        .formLogin(formLogin -> formLogin.disable())
                        .build();
        }
        }
