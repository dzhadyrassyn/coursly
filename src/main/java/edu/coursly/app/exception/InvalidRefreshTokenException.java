package edu.coursly.app.exception;

public class InvalidRefreshTokenException extends RuntimeException {

    public  InvalidRefreshTokenException(String message) {
        super(message);
    }
}
