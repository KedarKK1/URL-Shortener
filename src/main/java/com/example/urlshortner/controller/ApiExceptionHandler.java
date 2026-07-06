package com.example.urlshortner.controller;

import com.example.urlshortner.exception.AliasAlreadyExistsException;
import com.example.urlshortner.exception.ApiError;
import com.example.urlshortner.exception.ExpiredUrlException;
import com.example.urlshortner.exception.ResourceNotFoundException;
import java.time.Instant;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        return new ApiError(message, Instant.now());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotFound(ResourceNotFoundException ex) {
        return new ApiError(ex.getMessage(), Instant.now());
    }

    @ExceptionHandler(AliasAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleAliasConflict(AliasAlreadyExistsException ex) {
        return new ApiError(ex.getMessage(), Instant.now());
    }

    @ExceptionHandler(ExpiredUrlException.class)
    @ResponseStatus(HttpStatus.GONE)
    public ApiError handleExpired(ExpiredUrlException ex) {
        return new ApiError(ex.getMessage(), Instant.now());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleIllegalArgument(IllegalArgumentException ex) {
        return new ApiError(ex.getMessage(), Instant.now());
    }
}
