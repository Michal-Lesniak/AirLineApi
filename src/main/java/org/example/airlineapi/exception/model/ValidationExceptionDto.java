package org.example.airlineapi.exception.model;

import lombok.Value;

@Value
public class ValidationExceptionDto {
    private String code;
    private String field;
}
