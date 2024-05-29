package org.example.airlineapi.mapper;

import org.example.airlineapi.model.flight.Flight;
import org.example.airlineapi.model.flight.command.CreateFlightCommand;
import org.example.airlineapi.model.flight.dto.FlightDto;
import org.springframework.stereotype.Component;

@Component
public class FlightMapper {
    public static Flight fromCommand(CreateFlightCommand command){
        return Flight.builder()
                .flightNumber(command.flightNumber())
                .origin(command.origin())
                .destination(command.destination())
                .departureTime(command.departureTime())
                .arrivalTime(command.arrivalTime())
                .availableSeats(command.availableSeats())
                .build();
    }

    public static FlightDto toDto(Flight flight){
        return FlightDto.builder()
                .id(flight.getId())
                .flightNumber(flight.getFlightNumber())
                .origin(flight.getOrigin())
                .destination(flight.getDestination())
                .departureTime(flight.getDepartureTime())
                .arrivalTime(flight.getArrivalTime())
                .availableSeats(flight.getAvailableSeats())
                .build();
    }
}
