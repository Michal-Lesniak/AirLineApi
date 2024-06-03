package org.example.airlineapi.model.flight.command;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Value;

import java.time.LocalDateTime;

@Value
public class UpdateFlightTimeCommand {
    @NotNull(message = "NOT_NULL")
    @Future(message = "DATE_IN_PAST")
    LocalDateTime departureTime;

    @NotNull(message = "NOT_NULL")
    @Future(message = "DATE_IN_PAST")
    LocalDateTime arrivalTime;
}
