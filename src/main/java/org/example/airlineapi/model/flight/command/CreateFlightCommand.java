package org.example.airlineapi.model.flight.command;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.Value;

import java.time.LocalDateTime;

public record CreateFlightCommand(
        @NotNull(message = "NOT_NULL")
        @Pattern(regexp = "[A-z0-9]{0,9}", message = "PATTERN_MISMATCH_{regexp}")
        String flightNumber,

        @NotNull(message = "NOT_NULL")
        @Pattern(regexp = "[A-z0-9]{1,29}", message = "PATTERN_MISMATCH_{regexp}")
        String origin,

        @NotNull(message = "NOT_NULL")
        @Pattern(regexp = "[A-z0-9]{1,29}", message = "PATTERN_MISMATCH_{regexp}")
        String destination,

        @NotNull(message = "NOT_NULL")
        @Future(message = "DATE_IN_PAST")
        LocalDateTime departureTime,

        @NotNull(message = "NOT_NULL")
        @Future(message = "DATE_IN_PAST")
        LocalDateTime arrivalTime,

        @Positive(message = "NEGATIVE_VALUE")
        int availableSeats) {
}
