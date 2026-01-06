package com.devision.authentication.jwt.tokenStore;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import java.time.Duration;
@Service
public class CookieService {



        @Value("${app.auth.cookie-secure:false}")
        private boolean cookieSecure;

        @Value("${app.auth.refresh-token-expiration-ms:2592000000}")
        private long refreshTokenExpirationMs;

        public void setRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
            long maxAgeSeconds = refreshTokenExpirationMs / 1000;

            ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
                    .httpOnly(true)
                    .secure(cookieSecure)
                    .sameSite(cookieSecure ? "None" : "Lax")
                    .path("/auth/refresh")
                    .maxAge(Duration.ofSeconds(maxAgeSeconds))
                    .build();

            response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        }

        public void clearRefreshTokenCookie(HttpServletResponse response) {
            ResponseCookie cookie = ResponseCookie.from("refreshToken", "")
                    .httpOnly(true)
                    .secure(cookieSecure)
                    .sameSite(cookieSecure ? "None" : "Lax")
                    .path("/auth/refresh")
                    .maxAge(Duration.ZERO)
                    .build();

            response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        }

}
