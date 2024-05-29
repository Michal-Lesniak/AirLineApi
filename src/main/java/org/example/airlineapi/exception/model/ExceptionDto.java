package org.example.airlineapi.exception.model;

import lombok.Value;

import java.time.LocalDateTime;

@Value
public class ExceptionDto {
    private LocalDateTime timestamp = LocalDateTime.now();
    private String message;
}
