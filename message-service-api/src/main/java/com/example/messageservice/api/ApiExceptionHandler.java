package com.example.messageservice.api;

import com.example.messageservice.error.ApiErrorResponse;
import com.example.messageservice.error.BlankMessageException;
import com.example.messageservice.error.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(BlankMessageException.class)
    public ResponseEntity<ApiErrorResponse> handleBlankMessageException(
            BlankMessageException ex) {
        ApiErrorResponse response = ApiErrorResponse.builder()
                .error("error-0001")
                .message(ex.getMessage())
                .build();
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleUserNotFoundException(
            UserNotFoundException ex) {
        ApiErrorResponse response = ApiErrorResponse.builder()
                .error("error-0100")
                .message(ex.getMessage())
                .build();
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }
}
