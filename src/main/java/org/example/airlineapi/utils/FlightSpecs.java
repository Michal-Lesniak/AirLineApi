package org.example.airlineapi.utils;

import org.example.airlineapi.exception.ArgumentCannotBeNullException;
import org.example.airlineapi.model.flight.Flight;
import org.example.airlineapi.model.flight.FlightSearchCriteria;
import org.springframework.data.jpa.domain.Specification;

import java.text.MessageFormat;
import java.time.LocalDateTime;

public class FlightSpecs {
    private static Specification<Flight> equalFlightNumber(long flightNumber) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("flightNumber"), flightNumber);
    }

    private static Specification<Flight> equalOrigin(String origin) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("origin"), origin);
    }

    private static Specification<Flight> equalDestination(String destination) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("destination"), destination);
    }

    private static Specification<Flight> betweenDepartureTimes(LocalDateTime departureTimeFrom, LocalDateTime departureTimeTo) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.between(root.get("departureTime"), departureTimeFrom, departureTimeTo);
    }

    private static Specification<Flight> betweenArrivalTimes(LocalDateTime arrivalTimeFrom, LocalDateTime arrivalTimeTo) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.between(root.get("arrivalTime"), arrivalTimeFrom, arrivalTimeTo);
    }

    private static Specification<Flight> equalAvailableSeats(int availableSeats) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("availableSeats"), availableSeats);
    }

    public static Specification<Flight> createSpecs(FlightSearchCriteria criteria){
        if(criteria == null){
            throw new ArgumentCannotBeNullException("Flight search criteria cannot be null");
        }

        Specification<Flight> specs = Specification.where(null);

        if(criteria.getFlightNumber() != null){
            specs = specs.and(equalFlightNumber(criteria.getFlightNumber()));
        }

        if(criteria.getOrigin() != null && !criteria.getOrigin().isEmpty()){
            specs = specs.and(equalOrigin(criteria.getOrigin()));
        }

        if(criteria.getDestination() != null && !criteria.getDestination().isEmpty()){
            specs = specs.and(equalDestination(criteria.getDestination()));
        }

        if(criteria.getDepartureTimeFrom() != null && criteria.getDepartureTimeTo() != null){
            specs = specs.and(betweenDepartureTimes(criteria.getDepartureTimeFrom(), criteria.getDepartureTimeTo()));
        }

        if(criteria.getArrivalTimeFrom() != null && criteria.getArrivalTimeTo() != null){
            specs = specs.and(betweenArrivalTimes(criteria.getArrivalTimeFrom(), criteria.getArrivalTimeTo()));
        }

        if(criteria.getAvailableSeats() != null){
            specs = specs.and(equalAvailableSeats(criteria.getAvailableSeats()));
        }

        return specs;
    }

}
