package com.iambstha.tl_rest_api.resource;

import com.iambstha.tl_rest_api.domain.ApiResponse;
import com.iambstha.tl_rest_api.dto.RefreshTokenDto;
import com.iambstha.tl_rest_api.service.refreshToken.RefreshTokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Locale;

@Tag(name = "Refresh Token Resource", description = "API for managing refresh tokens")
@RestController
@RequestMapping("v1/api/refresh-token")
@RequiredArgsConstructor
public class RefreshTokenResource {

    @Autowired
    @Qualifier("token")
    private MessageSource messageSource;

    @Autowired
    private final RefreshTokenService service;

    private final Locale locale = LocaleContextHolder.getLocale();

    @PostMapping
    @Operation(summary = "Fetch token", description = "Generate refresh token")
    public ResponseEntity<ApiResponse> getTokens(
            @RequestBody RefreshTokenDto refreshTokenDto,
            HttpServletRequest request
    ) {

        ApiResponse apiResponse = ApiResponse.builder()
                .data(service.generateRefreshTokenAndToken(refreshTokenDto, request))
                .statusCode(200)
                .message(messageSource.getMessage("refresh_token_generation_success", null, locale))
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);

    }

}
