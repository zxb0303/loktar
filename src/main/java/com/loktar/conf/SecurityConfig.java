package com.loktar.conf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").authenticated()
                        .requestMatchers("/jellyfin/**").permitAll()
                        .requestMatchers("/qywx/callback/chatgpt/**").permitAll()
                        .requestMatchers("/qywx/callback/**").permitAll()
                        .requestMatchers("/synology/**").permitAll()
                        .requestMatchers("/test/**").permitAll()
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .defaultSuccessUrl("/swagger-ui/index.html", true)
                        .permitAll());
        return http.build();
    }
}
