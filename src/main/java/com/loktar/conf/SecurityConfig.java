package com.loktar.conf;

import com.loktar.filter.HeaderTokenAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final HeaderTokenAuthFilter headerTokenAuthFilter;

    public SecurityConfig(HeaderTokenAuthFilter headerTokenAuthFilter) {
        this.headerTokenAuthFilter = headerTokenAuthFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(CsrfConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/jellyfin/webhook.do",
                                "/certimate/webhook.do",
                                "/qywx/callback/chatgpt/receive.do",
                                "/qywx/callback/receive.do",
                                "/qywx/callback/patent/receive.do",
                                "/synology/sendMsg.do",
                                "/github/notifyMsg.do",
                                "/patentpdf/**",
                                "/patentpdfv2/**",
                                "/patentdoc/**",
                                "/test/**"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // token-based，不使用Session
                );
        http.addFilterBefore(headerTokenAuthFilter, UsernamePasswordAuthenticationFilter.class);
        http.formLogin(form -> form
                .defaultSuccessUrl("/swagger-ui/index.html", true)
                .permitAll());

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
