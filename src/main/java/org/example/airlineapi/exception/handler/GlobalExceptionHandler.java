package org.example.airlineapi.exception.handler;

import org.example.airlineapi.exception.AlreadyHaveTicketException;
import org.example.airlineapi.exception.DeleteOptimisticLockingException;
import org.example.airlineapi.exception.LackOfSeatException;
import org.example.airlineapi.exception.NotFoundException;
import org.example.airlineapi.exception.UpdateOptimisticLockingException;
import org.example.airlineapi.exception.model.ExceptionDto;
import org.example.airlineapi.exception.model.ValidationExceptionDto;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ExceptionDto handleNotFoundException(NotFoundException e) {
        return new ExceptionDto(e.getMessage());
    }

    @ExceptionHandler(UpdateOptimisticLockingException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ExceptionDto handleUpdateOptimisticLockingException(UpdateOptimisticLockingException e) {
        return new ExceptionDto(e.getMessage());
    }

    @ExceptionHandler(DeleteOptimisticLockingException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ExceptionDto handleDeleteOptimisticLockingException(DeleteOptimisticLockingException e) {
        return new ExceptionDto(e.getMessage());
    }

    @ExceptionHandler(LackOfSeatException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionDto handleLackOfSeatException(LackOfSeatException e) {
        return new ExceptionDto(e.getMessage());
    }

    @ExceptionHandler(AlreadyHaveTicketException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionDto handleAlreadyHaveTicketException(AlreadyHaveTicketException e) {
        return new ExceptionDto(e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public List<ValidationExceptionDto> handleValidationException(MethodArgumentNotValidException e) {
        return e.getBindingResult().getAllErrors()
                .stream()
                .map(error -> new ValidationExceptionDto(((FieldError) error).getField(), error.getDefaultMessage()))
                .toList();
    }
}
