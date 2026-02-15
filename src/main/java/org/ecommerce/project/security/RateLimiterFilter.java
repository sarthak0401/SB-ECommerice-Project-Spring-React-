package org.ecommerce.project.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.ecommerce.project.service.RedisRateLimiterService;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Order(1)
public class RateLimiterFilter extends OncePerRequestFilter {
    private final RedisRateLimiterService limiter;

    public RateLimiterFilter(RedisRateLimiterService limiter) {
        this.limiter = limiter;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String uri = request.getRequestURI();
        String ip = request.getRemoteAddr();

        boolean allowed;

        if(uri.startsWith("/api/auth/signin")){
            allowed = limiter.isAllowed("login:" + ip, 5,5,60);
        }
        else if(uri.startsWith("/api/public")){
            allowed = limiter.isAllowed("public:" + ip, 60, 60, 60);
        }
        else {
            allowed = limiter.isAllowed("api:" + ip, 120, 120, 120);
        }

        if(!allowed){
            response.setStatus(429);
            response.getWriter().write("Too many requests. Try again later.");
            return;
        }

        filterChain.doFilter(request, response);

    }
}
