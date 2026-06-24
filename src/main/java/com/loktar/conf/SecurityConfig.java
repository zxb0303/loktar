package com.loktar.conf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * 无需鉴权即可访问的公开端点（第三方回调、webhook、静态资源等）。
     */
    private static final String[] PUBLIC_ENDPOINTS = {
            "/jellyfin/webhook",
            "/certimate/webhook",
            "/synology/sendMsg",
            "/github/notifyMsg",
            "/qywx/callback/**",
            "/patentpdf/**",
            "/patentpdfv2/**",
            "/patentdoc/**",
            "/tiktok/getTimeParams",
            "/test/**"
    };

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(CsrfConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PUBLIC_ENDPOINTS).permitAll()
                        .anyRequest().authenticated())
                .httpBasic(Customizer.withDefaults());
        return http.build();
    }
}
