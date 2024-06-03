package org.example.airlineapi.model.person.command;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;

@Value
@Builder
public class CreatePersonCommand {

    @NotNull(message = "NOT_NULL")
    @Pattern(regexp = "[A-Z][a-z]{0,29}", message = "PATTERN_MISMATCH_{regexp}")
    private String firstName;

    @NotNull(message = "NOT_NULL")
    @Pattern(regexp = "[A-Z][a-z]{0,39}", message = "PATTERN_MISMATCH_{regexp}")
    private String lastName;

    @Email(message = "INVALID_EMAIL")
    private String email;

    @Pattern(regexp = "[0-9]{9}", message = "PATTERN_MISMATCH_{regexp}")
    private String phoneNumber;

    @Past(message = "DATE_IN_FUTURE")
    private LocalDate dateOfBirth;
}
