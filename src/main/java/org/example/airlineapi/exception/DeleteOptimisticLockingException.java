package org.example.airlineapi.exception;

public class DeleteOptimisticLockingException extends RuntimeException {
    public DeleteOptimisticLockingException() {
    }

    public DeleteOptimisticLockingException(String message) {
        super(message);
    }

    public DeleteOptimisticLockingException(String message, Throwable cause) {
        super(message, cause);
    }

    public DeleteOptimisticLockingException(Throwable cause) {
        super(cause);
    }
}
