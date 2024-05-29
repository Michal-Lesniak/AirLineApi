package org.example.airlineapi.exception;

public class LackOfSeatException extends RuntimeException{
    public LackOfSeatException() {
    }

    public LackOfSeatException(String message) {
        super(message);
    }

    public LackOfSeatException(String message, Throwable cause) {
        super(message, cause);
    }

    public LackOfSeatException(Throwable cause) {
        super(cause);
    }
}
