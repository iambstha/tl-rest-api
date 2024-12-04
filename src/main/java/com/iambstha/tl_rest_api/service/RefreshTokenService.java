package com.iambstha.tl_rest_api.service;

import com.iambstha.tl_rest_api.constant.AppConstant;
import com.iambstha.tl_rest_api.dto.RefreshTokenDto;
import com.iambstha.tl_rest_api.entity.RefreshToken;
import com.iambstha.tl_rest_api.entity.User;
import com.iambstha.tl_rest_api.exception.AuthException;
import com.iambstha.tl_rest_api.exception.ProcessingException;
import com.iambstha.tl_rest_api.exception.RecordNotFoundException;
import com.iambstha.tl_rest_api.repository.RefreshTokenRepository;
import com.iambstha.tl_rest_api.repository.UserRepository;
import com.iambstha.tl_rest_api.security.JwtUtil;
import com.iambstha.tl_rest_api.util.GeneralUtil;
import com.iambstha.tl_rest_api.util.UserUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.HashMap;

@Service
@RequiredArgsConstructor
@Transactional
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtUtil jwtUtil;
    private final UserLoadServiceImpl userLoadService;
    private final UserRepository userRepository;

    public RefreshTokenDto generateRefreshTokenAndToken(RefreshTokenDto refreshTokenDto, HttpServletRequest request) {
        if (!this.isValidToken(refreshTokenDto.getRefreshToken(), refreshTokenDto.getToken(), request)) {
            throw new AuthException("Token has expired.");
        }

        Claims claims = jwtUtil.extractAllClaims(refreshTokenDto.getToken());
        UserDetails userDetails = userLoadService.loadUserByUsername(claims.get("username").toString());
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RecordNotFoundException("User " + userDetails.getUsername() + " not found"));

        try {
            refreshTokenRepository.deleteExpiredTokens(GeneralUtil.getCurrentTs(), AppConstant.REFRESH_TOKEN_EXPIRATION_TIME);
        } catch (Exception e) {
            throw new ProcessingException("Deleting expired token failed.");
        }

        HashMap<String, Object> additionalClaims = new HashMap<>();
        additionalClaims.put("user_id", user.getUserId());
        additionalClaims.put("username", user.getUsername());

        String newToken = jwtUtil.generateToken(userDetails, additionalClaims);
        String newRefreshToken = jwtUtil.generateRefreshToken(userDetails, additionalClaims);
        while (this.refreshTokenExist(newRefreshToken))
            newRefreshToken = jwtUtil.generateRefreshToken(userDetails, additionalClaims);

        String hostAddress = request.getHeader("origin");

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(newToken);
        refreshToken.setRefreshToken(newRefreshToken);
        refreshToken.setCreatedTs(GeneralUtil.getCurrentTs());
        refreshToken.setCreatedBy(UserUtil.getUserId());
        refreshToken.setUserId(user.getUserId());
        refreshToken.setIpAddress(request.getRemoteAddr());
        refreshToken.setHostAddress(hostAddress == null ? "" : hostAddress);
        refreshToken.setUserAgent(request.getHeader("User-Agent"));

        refreshTokenRepository.save(refreshToken);

        return new RefreshTokenDto(newToken, newRefreshToken);
    }

    public boolean isValidToken(String refreshToken, String token, HttpServletRequest request) throws AuthException {
        Timestamp expirationTimestamp = refreshTokenRepository.expirationTimestamp(
                        AppConstant.REFRESH_TOKEN_EXPIRATION_TIME,
                        refreshToken,
                        token)
                .orElseThrow(() -> new RecordNotFoundException("Expiration time could not be found"));
        return GeneralUtil.getCurrentTs().before(expirationTimestamp);
    }

    public Boolean refreshTokenExist(String refreshToken) throws AuthException {
        return refreshTokenRepository.existsRefreshTokenByRefreshToken(refreshToken);
    }

    public Boolean deleteExpiredTokens() throws AuthException {
        refreshTokenRepository.deleteExpiredTokens(GeneralUtil.getCurrentTs(), AppConstant.REFRESH_TOKEN_EXPIRATION_TIME);
        return true;
    }

    public void deleteByRefreshToken(String refreshToken) throws AuthException {
        refreshTokenRepository.deleteByRefreshToken(refreshToken);
    }

    public boolean deleteUserToken(Long userId, String refreshToken, String token, HttpServletRequest request) {
        String hostAddress = request.getHeader("origin");
        hostAddress = hostAddress == null ? "" : hostAddress;
        Integer isDeleted = refreshTokenRepository.deleteUserToken(
                userId,
                refreshToken,
                token,
                request.getRemoteAddr(),
                hostAddress,
                request.getHeader("User-Agent"));

        return isDeleted > 0;
    }
}
