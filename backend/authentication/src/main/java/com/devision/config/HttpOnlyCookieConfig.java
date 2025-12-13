package com.devision.config;

import jakarta.servlet.http.Cookie;

public class HttpOnlyCookieConfig {

    private static final int ACCESS_TOKEN_AGE = 15 * 60;   // 15 min
    private static final int REFRESH_TOKEN_AGE = 7 * 24 * 60 * 60; // 7 days

    public static Cookie accessToken(String token) {
        Cookie cookie = new Cookie("access_token", token);
        cookie.setHttpOnly(true);
        cookie.setSecure(false); // true when HTTPS
        cookie.setPath("/");
        cookie.setMaxAge(ACCESS_TOKEN_AGE);
        return cookie;
    }

    public static Cookie refreshToken(String token) {
        Cookie cookie = new Cookie("refresh_token", token);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/auth/refresh"); // ⭐ isolate
        cookie.setMaxAge(REFRESH_TOKEN_AGE);
        return cookie;
    }
}
