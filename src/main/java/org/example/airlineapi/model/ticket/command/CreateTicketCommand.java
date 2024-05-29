package org.example.airlineapi.model.ticket.command;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Value;

@Value
public class CreateTicketCommand {

    @Positive(message = "NEGATIVE_OR_ZERO")
    private long seatNumber;

    @Positive(message = "NEGATIVE_OR_ZERO")
    private long ticketNumber;

    @NotNull(message = "NOT_NULL")
    @PositiveOrZero(message = "NEGATIVE")
    private double price;

    @Positive(message = "NEGATIVE_OR_ZERO")
    private long flightId;

    @Positive(message = "NEGATIVE_OR_ZERO")
    private long personId;
}