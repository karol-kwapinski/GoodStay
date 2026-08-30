package org.goodstay.exception;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.Map;
import java.util.NoSuchElementException;

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

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, String>> handleBadCredentials() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                Map.of("code", "INVALID_CREDENTIALS")
        );
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Void> handleNoSuchElementException() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @ExceptionHandler(InvalidDateRangeException.class)
    public ResponseEntity<Map<String, String>> handleInvalidDateRangeException() {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                Map.of("code", "INVALID_DATE_RANGE")
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleMethodArgumentNotValid() {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                Map.of("code", "METHOD_ARGUMENT_NOT_VALID")
        );
    }

    @ExceptionHandler(RoomNotAvailableException.class)
    public ResponseEntity<Map<String, String>> handleRoomNotAvailableException() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                Map.of("code", "ROOM_NOT_AVAILABLE")
        );
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleUserNotFoundException() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                Map.of("code", "USER_NOT_FOUND")
        );
    }

    @ExceptionHandler(InvalidRoomTypeSelectionException.class)
    public ResponseEntity<Map<String, String>> handleInvalidRoomTypeSelectionException() {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                Map.of("code", "INVALID_ROOM_TYPE")
        );
    }

    @ExceptionHandler(InvalidRoomQuantityException.class)
    public ResponseEntity<Map<String, String>> handleInvalidRoomQuantityException() {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                Map.of("code", "INVALID_ROOM_QUANTITY")
        );
    }

    @ExceptionHandler(ReviewAlreadyExistsException.class)
    public ResponseEntity<Map<String, String>> handleReviewAlreadyExistsException() {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                Map.of("code", "REVIEW_ALREADY_EXISTS")
        );
    }

    @ExceptionHandler(HotelDoesNotExistException.class)
    public ResponseEntity<Map<String, String>> handleHotelDoesNotExistException() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                Map.of("code", "HOTEL_DOES_NOT_EXIST")
        );
    }

    @ExceptionHandler(InvalidTimeRangeException.class)
    public ResponseEntity<Map<String, String>> handleInvalidTimeRangeException() {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                Map.of("code", "INVALID_TIME_RANGE")
        );
    }

    @ExceptionHandler(HotelWithSameLocationDataAlreadyExistsException.class)
    public ResponseEntity<Map<String, String>> handleHotelWithSameLocationDataAlreadyExistsException() {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                Map.of("code", "INVALID_HOTEL_DATA")
        );
    }
}