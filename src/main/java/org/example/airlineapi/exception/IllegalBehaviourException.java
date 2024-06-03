package org.example.airlineapi.exception;

public class IllegalBehaviourException extends RuntimeException {
    public IllegalBehaviourException() {
    }

    public IllegalBehaviourException(String message) {
        super(message);
    }

    public IllegalBehaviourException(String message, Throwable cause) {
        super(message, cause);
    }

    public IllegalBehaviourException(Throwable cause) {
        super(cause);
    }
}
