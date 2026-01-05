package com.devision.authentication.jwt.tokenStore;

import com.devision.authentication.jwt.JwtService;
import io.jsonwebtoken.Claims;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;

@Service
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository, JwtService jwtService) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtService = jwtService;
    }

    public RefreshToken save(String userId, String refreshToken) {
        Claims claims = jwtService.parseClaims(refreshToken);
        RefreshToken token = RefreshToken.builder()
                .userId(userId)
                .token(refreshToken)
                .expiryDate(claims.getExpiration())
                .revoked(false)
                .build();

        return refreshTokenRepository.save(token);

    }

    public RefreshToken validate(String refreshToken) {
        if(!jwtService.isRefreshToken(refreshToken)){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token is invalid");

        }
        RefreshToken stored  = refreshTokenRepository.findByTokenAndRevokedFalse(refreshToken)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Refresh token not found"));
        if(stored.getExpiryDate().before(new Date())){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token is expired");
        }
        return stored;
    }

    public void revokeRefreshToken(RefreshToken token) {
        token.setRevoked(true);
        refreshTokenRepository.save(token);
    }
}
