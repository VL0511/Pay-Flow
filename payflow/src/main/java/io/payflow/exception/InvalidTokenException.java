package io.payflow.exception;

public class InvalidTokenException extends RuntimeException {
    public InvalidTokenException(String message) {
        super("The provided token is invalid.");
    }
}
