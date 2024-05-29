package org.example.airlineapi.model.ticket;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class TicketSearchCriteria {
    private long seatNumber;
    private long ticketNumber;
    private double price;
}
