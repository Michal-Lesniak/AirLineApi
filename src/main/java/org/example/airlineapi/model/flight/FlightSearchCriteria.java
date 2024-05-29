package org.example.airlineapi.model.flight;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

@Value
@Builder
public class FlightSearchCriteria {
    private Long flightNumber;
    private String origin;
    private String destination;
    private LocalDateTime departureTimeFrom;
    private LocalDateTime departureTimeTo;
    private LocalDateTime arrivalTimeFrom;
    private LocalDateTime arrivalTimeTo;
    private Integer availableSeats;
}