package org.goodstay.exception;

public class PasswordMismatchException extends RuntimeException {

    public PasswordMismatchException() {
        super("PASSWORD_MISMATCH");
    }
}