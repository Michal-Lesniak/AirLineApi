package org.example.airlineapi.exception;

public class UpdateOptimisticLockingException extends RuntimeException {
    public UpdateOptimisticLockingException() {
    }

    public UpdateOptimisticLockingException(String message) {
        super(message);
    }

    public UpdateOptimisticLockingException(String message, Throwable cause) {
        super(message, cause);
    }

    public UpdateOptimisticLockingException(Throwable cause) {
        super(cause);
    }
}
