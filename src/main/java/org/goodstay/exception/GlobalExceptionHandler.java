package org.goodstay.exception;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PasswordMismatchException.class)
    public ResponseEntity<Void> handlePasswordMismatch() {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<Map<String, String>> handleEmailExists() {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                Map.of("code", "EMAIL_ALREADY_EXISTS"));
    }
}