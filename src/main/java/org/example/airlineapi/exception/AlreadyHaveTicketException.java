package org.example.airlineapi.exception;

public class AlreadyHaveTicketException extends RuntimeException {
    public AlreadyHaveTicketException() {
    }

    public AlreadyHaveTicketException(String message) {
        super(message);
    }

    public AlreadyHaveTicketException(String message, Throwable cause) {
        super(message, cause);
    }

    public AlreadyHaveTicketException(Throwable cause) {
        super(cause);
    }
}
