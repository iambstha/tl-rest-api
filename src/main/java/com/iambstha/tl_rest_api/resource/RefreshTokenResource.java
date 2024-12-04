package com.iambstha.tl_rest_api.resource;

import com.iambstha.tl_rest_api.constant.AppConstant;
import com.iambstha.tl_rest_api.constant.StatusConstants;
import com.iambstha.tl_rest_api.domain.ApiResponse;
import com.iambstha.tl_rest_api.dto.RefreshTokenDto;
import com.iambstha.tl_rest_api.security.JwtUtil;
import com.iambstha.tl_rest_api.service.RefreshTokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Tag(name = "Refresh Token Resource", description = "API for managing refresh tokens")
@RestController
@RequestMapping("v1/api/refresh-token")
@RequiredArgsConstructor
public class RefreshTokenResource {

    @Autowired
    @Qualifier("token")
    private MessageSource messageSource;

    private final RefreshTokenService refreshTokenService;
    private final JwtUtil jwtUtil;

    private final Locale locale = LocaleContextHolder.getLocale();

    @PostMapping
    @Operation(summary = "Get tokens", description = "Fetch tokens")
    public ResponseEntity<ApiResponse> getTokens(
            @RequestBody RefreshTokenDto refreshTokenDto,
            HttpServletRequest request
    ) {
        List<String> details = new ArrayList<>();
        ApiResponse response = new ApiResponse(StatusConstants.FAILED, 400, AppConstant.DEFAULT_ERROR, null, null, null);

        try {
            RefreshTokenDto refreshTokenResponse = refreshTokenService.generateRefreshTokenAndToken(refreshTokenDto, request);
            if (refreshTokenResponse.getRefreshToken() != null && refreshTokenResponse.getToken() != null) {
                refreshTokenService.deleteByRefreshToken(refreshTokenDto.getRefreshToken());
                response.setStatus(StatusConstants.SUCCESS);
                response.setData(refreshTokenResponse);
                response.setStatusCode(200);
                response.setMessage(messageSource.getMessage("refresh_token_generation_success", null, locale));
            }
        } catch (Exception e) {
            response.setErrorMessage(e.getMessage());
        }

        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getStatusCode()));

    }

}
