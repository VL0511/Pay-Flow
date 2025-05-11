package io.payflow.exception;

public class ExpiredTokenException extends RuntimeException {
    public ExpiredTokenException(String message) {
        super("The provided token is expired: ");
    }
}
