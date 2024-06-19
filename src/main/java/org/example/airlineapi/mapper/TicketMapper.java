package org.example.airlineapi.mapper;

import org.example.airlineapi.model.ticket.Ticket;
import org.example.airlineapi.model.ticket.command.CreateTicketCommand;
import org.example.airlineapi.model.ticket.dto.TicketDto;
import org.springframework.stereotype.Component;

@Component
public class TicketMapper {

    public static Ticket fromCommand(CreateTicketCommand command) {
        return Ticket.builder()
                .seatNumber(command.getSeatNumber())
                .price(command.getPrice())
                .build();
    }

    public static TicketDto toDto(Ticket ticket) {
        return TicketDto.builder()
                .id(ticket.getId())
                .ticketNumber(ticket.getTicketNumber())
                .seatNumber(ticket.getSeatNumber())
                .price(ticket.getPrice())
                .person(PersonMapper.toSimpleDto(ticket.getPerson()))
                .flight(FlightMapper.toSimpleDto(ticket.getFlight()))
                .build();
    }


}
