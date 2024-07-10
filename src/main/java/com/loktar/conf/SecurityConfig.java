package com.loktar.conf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(CsrfConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/jellyfin/webhook.do",
                                "/qywx/callback/chatgpt/receive.do",
                                "/qywx/callback/receive.do",
                                "/synology/sendMsg.do",
                                "/github/notifyMsg.do",
                                "/patentpdf/get.do",
                                "/patentpdf/set.do",
                                "/patentpdf/getEncodeDetails.do",
                                "/patentpdf/getContractDTO.do",
                                "/test/**"
                        ).permitAll()
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .defaultSuccessUrl("/swagger-ui/index.html", true)
                        .permitAll());
        return http.build();
    }
}
