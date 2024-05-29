package org.example.airlineapi.model.ticket.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class TicketDto {
    private long id;
    private long seatNumber;
    private long ticketNumber;
    private double price;
}
