package org.goodstay.exception;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PasswordMismatchException.class)
    ResponseEntity<Void> handlePasswordMismatch() {
        return ResponseEntity.badRequest().build();
    }
    @ExceptionHandler(EmailAlreadyExistsException.class)
    ResponseEntity<Void> handleEmailExists() {
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }
}