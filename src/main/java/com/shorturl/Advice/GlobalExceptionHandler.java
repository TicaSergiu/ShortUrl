package com.shorturl.Advice;

import com.shorturl.Exceptions.CustomUrlAlreadyExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.util.InvalidUrlException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidUrlException.class)
    public ResponseEntity<String> handleInvalidUrl(InvalidUrlException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                             .body("Cannot shorten the URL. The URL cannot be empty and it should be a valid URL address");
    }

    @ExceptionHandler(CustomUrlAlreadyExistsException.class)
    public ResponseEntity<String> handleAlreadyExistingUrl(CustomUrlAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

}
