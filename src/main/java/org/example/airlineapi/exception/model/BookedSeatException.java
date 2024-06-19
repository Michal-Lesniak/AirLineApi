package org.example.airlineapi.exception.model;

public class BookedSeatException extends RuntimeException{
    public BookedSeatException() {
    }

    public BookedSeatException(String message) {
        super(message);
    }

    public BookedSeatException(String message, Throwable cause) {
        super(message, cause);
    }

    public BookedSeatException(Throwable cause) {
        super(cause);
    }
}
