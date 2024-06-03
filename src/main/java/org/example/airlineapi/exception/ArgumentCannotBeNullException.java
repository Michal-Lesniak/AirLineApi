package org.example.airlineapi.exception;

public class ArgumentCannotBeNullException extends RuntimeException{
    public ArgumentCannotBeNullException() {}

    public ArgumentCannotBeNullException(String message) {
        super(message);
    }

    public ArgumentCannotBeNullException(String message, Throwable cause) {
        super(message, cause);
    }

    public ArgumentCannotBeNullException(Throwable cause) {
        super(cause);
    }
}
