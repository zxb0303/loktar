package com.loktar.filter;

import com.loktar.conf.LokTarConfig;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
@Component
public class HeaderTokenAuthFilter extends OncePerRequestFilter {

    private final LokTarConfig lokTarConfig;

    public HeaderTokenAuthFilter(LokTarConfig lokTarConfig) {
        this.lokTarConfig = lokTarConfig;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String headerAuth = request.getHeader("Authorization");
        if (headerAuth != null && headerAuth.startsWith("Bearer ")) {
            String token = headerAuth.substring(7);
            if (lokTarConfig.getSecurity().getToken().equals(token)) {
                Authentication auth = new UsernamePasswordAuthenticationToken(
                        "apiUser",
                        null,
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
                );
                SecurityContextHolder.getContext().setAuthentication(auth);
            } else {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token.");
                return;
            }
        } else {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token missing.");
            return;
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // 只对 /azure/xiaozhiTTS.do 接口进行token认证
        return !request.getServletPath().equals("/azure/xiaozhiTTS.do");
    }
}
