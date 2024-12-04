package com.iambstha.tl_rest_api.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iambstha.tl_rest_api.domain.ApiResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint, Serializable {

    private final MessageSource messageSource;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException, ServletException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        Locale locale = LocaleContextHolder.getLocale();
        Object jwtException = request.getAttribute("jwtException");
        final String message = (jwtException != null) ? jwtException.toString() : authException.getLocalizedMessage();

        List<String> details = new ArrayList<>();
        details.add(message);
        ApiResponse error = new ApiResponse(
                "FAILED",
                HttpStatus.UNAUTHORIZED.value(),
                messageSource.getMessage("login_error", null, locale),
                details,
                null,
                null);

        OutputStream out = response.getOutputStream();

        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(out, error);
        out.flush();
    }

}
