package com.devision.authentication.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class PreControllerLogFilter extends OncePerRequestFilter {

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // ✅ ONLY log login/register/refresh to avoid noisy logs
        String path = request.getServletPath();
        return !(path.equals("/auth/login")
                || path.equals("/auth/register")
                || path.equals("/auth/refresh"));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        log.info("[PRE] {} {} contentType={} contentLength={}",
                request.getMethod(),
                request.getRequestURI(),
                request.getContentType(),
                request.getContentLengthLong()
        );

        Collections.list(request.getHeaderNames()).forEach(h ->
                log.info("[PRE] header {}: {}", h, request.getHeader(h))
        );

        filterChain.doFilter(request, response);

        log.info("[PRE] response status={}", response.getStatus());
    }
}
