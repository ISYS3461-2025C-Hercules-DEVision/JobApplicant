package com.devision.config;

import jakarta.servlet.http.Cookie;

public class HttpOnlyCookieConfig {

    private static final int ACCESS_TOKEN_AGE = 15 * 60;
    private static final int REFRESH_TOKEN_AGE = 7 * 24 * 60 * 60;

    public static Cookie accessToken(String token) {
        Cookie cookie = new Cookie("access_token", token);
        cookie.setHttpOnly(true);
        cookie.setSecure(false); // true in prod (HTTPS)
        cookie.setPath("/");
        cookie.setMaxAge(ACCESS_TOKEN_AGE);
        return cookie;
    }

    public static Cookie refreshToken(String token) {
        Cookie cookie = new Cookie("refresh_token", token);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/auth/refresh");
        cookie.setMaxAge(REFRESH_TOKEN_AGE);
        return cookie;
    }
}

