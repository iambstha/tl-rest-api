package com.iambstha.tl_rest_api.service.refreshToken;

import com.iambstha.tl_rest_api.dto.RefreshTokenDto;
import com.iambstha.tl_rest_api.exception.AuthException;
import jakarta.servlet.http.HttpServletRequest;

import java.sql.Timestamp;
import java.util.Optional;

public interface RefreshTokenService {

    Optional<Timestamp> getExpirationTimestamp(Long expirationTime, String refreshToken, String token);

    void deleteExpiredTokens(Timestamp currentTimestamp, Long expirationTime);

    Boolean existsRefreshToken(String refreshToken);

    void deleteByRefreshToken(String refreshToken);

    RefreshTokenDto generateRefreshTokenAndToken(RefreshTokenDto refreshTokenDto, HttpServletRequest request);

    boolean isValidToken(String refreshToken, String token, HttpServletRequest request) throws AuthException;

    Boolean refreshTokenExist(String refreshToken) throws AuthException;
}
