package org.example.airlineapi.model.flight.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class SimpleFlightDto {
    String flightNumber;
    String origin;
    String destination;
}
