package com.iambstha.tl_rest_api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class FileUploadException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -655790452141010900L;

    public FileUploadException(String exception) {
        super(exception);
    }

}