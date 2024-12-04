package com.iambstha.tl_rest_api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class AuthException extends RuntimeException {
    /**
     *
     */
    @Serial
    private static final long serialVersionUID = 3909083199557817547L;

    public AuthException(String message) {
        super(message);
    }
}
