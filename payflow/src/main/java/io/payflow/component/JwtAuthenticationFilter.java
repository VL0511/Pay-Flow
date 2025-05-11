package io.payflow.component;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request
            , HttpServletResponse response
            , FilterChain filterChain) throws ServletException, IOException {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            Cookie jwtCookie = Arrays.stream(cookies)
                    .filter(cookie -> "token".equals(cookie.getName()))
                    .findFirst()
                    .orElse(null);

            if (jwtCookie != null) {
                String token = jwtCookie.getValue();
                if (jwtTokenProvider.validateToken(token)) {
                    var authentication = jwtTokenProvider.getAuthentication(token);
                    if (authentication instanceof UsernamePasswordAuthenticationToken) {
                        ((UsernamePasswordAuthenticationToken) authentication)
                                .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    }
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }

        filterChain.doFilter(request, response);
    }
}
