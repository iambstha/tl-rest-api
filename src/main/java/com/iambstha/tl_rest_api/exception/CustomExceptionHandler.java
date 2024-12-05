package com.iambstha.tl_rest_api.exception;

import com.iambstha.tl_rest_api.constant.StatusConstants;
import com.iambstha.tl_rest_api.domain.ApiResponse;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        List<String> details = new ArrayList<>();
        details.add(ex.getMessage());
        details.add(Objects.requireNonNull(ex.getRootCause()).getMessage());
        ApiResponse error = new ApiResponse(StatusConstants.FAILED, "MALFORMED_JSON_REQUEST", details);
        error.setStatusCode(HttpStatus.BAD_REQUEST.value());
        error.setErrorMessage("Some error in json request");
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        List<String> details = new ArrayList<>();
        for (ObjectError error : ex.getBindingResult().getAllErrors()) {
            details.add(error.getDefaultMessage());
        }

        ApiResponse error = new ApiResponse(StatusConstants.FAILED, "VALIDATION_FAILED", details);
        error.setStatusCode(HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        List<String> details = new ArrayList<>();
        details.add(ex.getParameterName() + " parameter is missing");

        ApiResponse error = new ApiResponse(StatusConstants.FAILED, "MISSING_PARAMETER", details);
        error.setStatusCode(HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        List<String> details = new ArrayList<>();
        details.add(ex.getLocalizedMessage());
        ApiResponse error = new ApiResponse(StatusConstants.FAILED, "METHOD_NOT_SUPPORTED", details);
        error.setStatusCode(HttpStatus.METHOD_NOT_ALLOWED.value());
        return new ResponseEntity<>(error, HttpStatus.METHOD_NOT_ALLOWED);
    }

    @Override
    protected ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        List<String> details = new ArrayList<>();
        details.add(ex.getLocalizedMessage());
        ApiResponse error = new ApiResponse(StatusConstants.FAILED, "NOT_FOUND", details);
        error.setStatusCode(HttpStatus.NOT_FOUND.value());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @Override
    protected ResponseEntity<Object> handleHandlerMethodValidationException(HandlerMethodValidationException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        List<String> errorMessages = new ArrayList<>();
        errorMessages = ex.getAllErrors().stream().map(MessageSourceResolvable::getDefaultMessage).collect(Collectors.toList());

        ApiResponse error = new ApiResponse(StatusConstants.FAILED, "VALIDATION_FAILED", errorMessages);
        error.setStatusCode(HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleAllExceptions(Exception ex, WebRequest req) {
        List<String> details = new ArrayList<>();
        details.add(ex.getLocalizedMessage());
        ApiResponse error = new ApiResponse(StatusConstants.FAILED, "SERVER_ERROR", details);
        error.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(InvalidDataAccessApiUsageException.class)
    public ResponseEntity<ApiResponse> handleInvalidDataAccessApiUsageException(InvalidDataAccessApiUsageException ex, WebRequest req) {
        ApiResponse error = new ApiResponse(StatusConstants.FAILED, "INVALID_DATA_ACCESS", null);
        error.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse> handleDataIntegrityViolationException(DataIntegrityViolationException ex, WebRequest req) {
        ApiResponse error = new ApiResponse(StatusConstants.FAILED, ex.getLocalizedMessage(), null);
        error.setStatusCode(HttpStatus.CONFLICT.value());
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException ex) {
        List<String> details = new ArrayList<>();
        for (ConstraintViolation<?> error : ex.getConstraintViolations()) {
            details.add(error.getMessage());
        }

        ApiResponse error = new ApiResponse(StatusConstants.FAILED, "VALIDATION_FAILED", details);
        error.setStatusCode(HttpStatus.UNPROCESSABLE_ENTITY.value());
        return new ResponseEntity<>(error, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiResponse> handleEntityNotFound(EntityNotFoundException ex) {
        List<String> details = new ArrayList<>();
        details.add(ex.getMessage());
        ApiResponse error = new ApiResponse(StatusConstants.FAILED, "ENTITY_NOT_FOUND", details);
        error.setStatusCode(HttpStatus.NOT_FOUND.value());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(EmptyResultDataAccessException.class)
    public ResponseEntity<ApiResponse> handleEmptyResultDataAccessException(EmptyResultDataAccessException ex) {
        List<String> details = new ArrayList<>();
        details.add(ex.getMessage());
        ApiResponse error = new ApiResponse(StatusConstants.FAILED, "DATA_NOT_FOUND", details);
        error.setStatusCode(HttpStatus.NOT_FOUND.value());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex, WebRequest request) {
        List<String> details = new ArrayList<>();
        if (ex.getRequiredType() != null) {
            details.add(ex.getName() + " should be of type " + ex.getRequiredType().getName());
        }

        ApiResponse error = new ApiResponse(StatusConstants.FAILED, "ARGUMENT_TYPE_MISMATCH", details);
        error.setStatusCode(HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RecordNotFoundException.class)
    public ResponseEntity<ApiResponse> handleRecordNotFoundException(RecordNotFoundException ex, WebRequest req) {
        ApiResponse error = new ApiResponse(StatusConstants.FAILED, ex.getLocalizedMessage(), null);
        error.setStatusCode(HttpStatus.NOT_FOUND.value());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<ApiResponse> handleAuthException(AuthException ex, WebRequest re) {
        List<String> details = new ArrayList<>();
        details.add(ex.getMessage());
        ApiResponse error = new ApiResponse(StatusConstants.FAILED, "AUTH_ERROR", details);
        error.setStatusCode(HttpStatus.UNAUTHORIZED.value());
        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(TokenExpireException.class)
    public ResponseEntity<ApiResponse> handleTokenExpireException(TokenExpireException ex, WebRequest re) {
        List<String> details = new ArrayList<>();
        details.add(ex.getMessage());
        ApiResponse error = new ApiResponse(StatusConstants.FAILED, "TOKEN_EXPIRED", details);
        error.setStatusCode(HttpStatus.UNAUTHORIZED.value());
        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(NotAllowedException.class)
    public ResponseEntity<ApiResponse> handleNotAllowedException(NotAllowedException ex, WebRequest re) {
        List<String> details = new ArrayList<>();
        details.add(ex.getMessage());
        ApiResponse error = new ApiResponse(StatusConstants.FAILED, "NOT_ALLOWED", details);
        error.setStatusCode(HttpStatus.FORBIDDEN.value());
        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(ProcessingException.class)
    public ResponseEntity<ApiResponse> handleProcessingException(ProcessingException ex, WebRequest re) {
        List<String> details = new ArrayList<>();
        details.add(ex.getMessage());
        ApiResponse error = new ApiResponse(StatusConstants.FAILED, "PROCESSING_ERROR", details);
        error.setStatusCode(HttpStatus.EXPECTATION_FAILED.value());
        return new ResponseEntity<>(error, HttpStatus.EXPECTATION_FAILED);
    }

    @ExceptionHandler(FileSizeExceededException.class)
    public ResponseEntity<ApiResponse> handleFileSizeExceededException(FileSizeExceededException ex, WebRequest re) {
        List<String> details = new ArrayList<>();
        details.add(ex.getMessage());
        ApiResponse error = new ApiResponse(StatusConstants.FAILED, "FILE_SIZE_EXCEEDED", details);
        error.setStatusCode(HttpStatus.EXPECTATION_FAILED.value());
        return new ResponseEntity<>(error, HttpStatus.EXPECTATION_FAILED);
    }

    @ExceptionHandler(FileUploadException.class)
    public ResponseEntity<ApiResponse> handleFileUploadException(FileUploadException ex, WebRequest re) {
        List<String> details = new ArrayList<>();
        details.add(ex.getMessage());
        ApiResponse error = new ApiResponse(StatusConstants.FAILED, "FILE_UPLOAD_ERROR", details);
        error.setStatusCode(HttpStatus.EXPECTATION_FAILED.value());
        return new ResponseEntity<>(error, HttpStatus.EXPECTATION_FAILED);
    }

    @ExceptionHandler(TimeoutException.class)
    public ResponseEntity<ApiResponse> handleTimeOutException(TimeoutException ex, WebRequest re) {
        List<String> details = new ArrayList<>();
        details.add(ex.getMessage());
        ApiResponse error = new ApiResponse(StatusConstants.FAILED, "TIMEOUT_ERROR", details);
        error.setStatusCode(HttpStatus.REQUEST_TIMEOUT.value());
        return new ResponseEntity<>(error, HttpStatus.REQUEST_TIMEOUT);
    }

}
