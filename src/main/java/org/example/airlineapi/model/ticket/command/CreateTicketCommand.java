package org.example.airlineapi.model.ticket.command;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder
public class CreateTicketCommand {

    @Positive(message = "NEGATIVE_OR_ZERO")
    @NotNull(message = "NOT_NULL")
    private long seatNumber;

    @NotNull(message = "NOT_NULL")
    @PositiveOrZero(message = "NEGATIVE")
    private BigDecimal price;

    @Positive(message = "NEGATIVE_OR_ZERO")
    @NotNull(message = "NOT_NULL")
    private long personId;
}
