package com.iambstha.tl_rest_api.service.refreshToken;

import com.iambstha.tl_rest_api.constant.AppConstant;
import com.iambstha.tl_rest_api.dto.RefreshTokenDto;
import com.iambstha.tl_rest_api.entity.RefreshToken;
import com.iambstha.tl_rest_api.entity.User;
import com.iambstha.tl_rest_api.exception.AuthException;
import com.iambstha.tl_rest_api.exception.ProcessingException;
import com.iambstha.tl_rest_api.exception.RecordNotFoundException;
import com.iambstha.tl_rest_api.repository.refreshToken.MySqlRefreshTokenRepository;
import com.iambstha.tl_rest_api.repository.UserRepository;
import com.iambstha.tl_rest_api.security.JwtUtil;
import com.iambstha.tl_rest_api.service.UserLoadServiceImpl;
import com.iambstha.tl_rest_api.util.GeneralUtil;
import com.iambstha.tl_rest_api.util.UserUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Optional;

@Service
@ConditionalOnProperty(name = "spring.datasource.platform", havingValue = "mysql")
@RequiredArgsConstructor
public class MySqlRefreshTokenService implements RefreshTokenService {

    @Autowired
    private final MySqlRefreshTokenRepository repository;

    @Autowired
    private final JwtUtil jwtUtil;

    @Autowired
    private final UserLoadServiceImpl userLoadService;

    @Autowired
    private final UserRepository userRepository;

    @Override
    public Optional<Timestamp> getExpirationTimestamp(Long expirationTime, String refreshToken, String token) {
        return repository.expirationTimestamp(expirationTime, refreshToken, token);
    }

    @Override
    public void deleteExpiredTokens(Timestamp currentTimestamp, Long expirationTime) {
        repository.deleteExpiredTokens(currentTimestamp, expirationTime);
    }

    @Override
    public Boolean existsRefreshToken(String refreshToken) {
        return repository.existsRefreshTokenByRefreshToken(refreshToken);
    }

    @Override
    public void deleteByRefreshToken(String refreshToken) {
        repository.deleteByRefreshToken(refreshToken);
    }

    @Override
    public RefreshTokenDto generateRefreshTokenAndToken(RefreshTokenDto refreshTokenDto, HttpServletRequest request) {
        if (!this.isValidToken(refreshTokenDto.getRefreshToken(), refreshTokenDto.getToken(), request)) {
            throw new AuthException("Token has expired.");
        }

        Claims claims = jwtUtil.extractAllClaims(refreshTokenDto.getToken());
        UserDetails userDetails = userLoadService.loadUserByUsername(claims.get("username").toString());
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RecordNotFoundException("User " + userDetails.getUsername() + " not found"));

        try {
            repository.deleteExpiredTokens(GeneralUtil.getCurrentTs(), AppConstant.REFRESH_TOKEN_EXPIRATION_TIME);
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

        repository.save(refreshToken);

        return new RefreshTokenDto(newToken, newRefreshToken);
    }

    @Override
    public boolean isValidToken(String refreshToken, String token, HttpServletRequest request) throws AuthException {
        Timestamp expirationTimestamp = repository.expirationTimestamp(
                        AppConstant.REFRESH_TOKEN_EXPIRATION_TIME,
                        refreshToken,
                        token)
                .orElseThrow(() -> new RecordNotFoundException("Expiration time could not be found"));
        return GeneralUtil.getCurrentTs().before(expirationTimestamp);
    }

    @Override
    public Boolean refreshTokenExist(String refreshToken) throws AuthException {
        return repository.existsRefreshTokenByRefreshToken(refreshToken);
    }

}
