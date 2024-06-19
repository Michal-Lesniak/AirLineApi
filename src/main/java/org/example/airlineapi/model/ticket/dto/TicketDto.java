package org.example.airlineapi.model.ticket.dto;

import lombok.Builder;
import lombok.Value;
import org.example.airlineapi.model.flight.dto.SimpleFlightDto;
import org.example.airlineapi.model.person.dto.SimplePersonDto;

import java.math.BigDecimal;

@Value
@Builder
public class TicketDto {
    private long id;
    private SimplePersonDto person;
    private SimpleFlightDto flight;
    private long seatNumber;
    private String ticketNumber;
    private BigDecimal price;
}
