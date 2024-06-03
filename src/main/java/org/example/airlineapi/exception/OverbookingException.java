package org.example.airlineapi.exception;

public class OverbookingException extends RuntimeException{
    public OverbookingException() {
    }

    public OverbookingException(String message) {
        super(message);
    }

    public OverbookingException(String message, Throwable cause) {
        super(message, cause);
    }

    public OverbookingException(Throwable cause) {
        super(cause);
    }
}
