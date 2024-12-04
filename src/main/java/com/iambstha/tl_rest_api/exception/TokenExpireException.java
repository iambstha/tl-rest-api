package com.iambstha.tl_rest_api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class TokenExpireException extends RuntimeException {

    public TokenExpireException(String message) {
        super(message);
    }

}
