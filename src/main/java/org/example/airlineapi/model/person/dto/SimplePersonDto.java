package org.example.airlineapi.model.person.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class SimplePersonDto {
    private String firstName;
    private String lastName;
}
