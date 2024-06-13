package org.example.airlineapi.utils.Specification;

import org.example.airlineapi.exception.ArgumentCannotBeNullException;
import org.example.airlineapi.model.ticket.Ticket;
import org.example.airlineapi.model.ticket.TicketSearchCriteria;
import org.springframework.data.jpa.domain.Specification;

public class TicketSpecs {

    private static Specification<Ticket> equalSeatNumber(long seatNumber) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("seatNumber"), seatNumber);
    }

    private static Specification<Ticket> equalTicketNumber(long ticketNumber) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("ticketNumber"), ticketNumber);
    }

    private static Specification<Ticket> equalPrice(double price) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("price"), price);
    }

    private static Specification<Ticket> hasFlightId(long flightId) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("flight").get("id"), flightId);
    }

    private static Specification<Ticket> hasPersonId(long personId) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("person").get("id"), personId);
    }

    private static Specification<Ticket> createSpecs(TicketSearchCriteria criteria) {
        if (criteria == null) {
            throw new ArgumentCannotBeNullException("Ticket search criteria cannot be null");
        }

        Specification<Ticket> specs = Specification.where(null);

        if(criteria.getSeatNumber() != 0) {
            specs = specs.and(equalSeatNumber(criteria.getSeatNumber()));
        }

        if(criteria.getTicketNumber() != 0) {
            specs = specs.and(equalTicketNumber(criteria.getTicketNumber()));
        }

        if(criteria.getPrice() != 0) {
            specs = specs.and(equalPrice(criteria.getPrice()));
        }

        return specs;
    }

    public static Specification<Ticket> specsWithPersonId(long personId, TicketSearchCriteria criteria) {
        Specification<Ticket> specs = createSpecs(criteria);
        return specs.and(hasPersonId(personId));

    }

    public static Specification<Ticket> specsWithFlightId(long flightId, TicketSearchCriteria criteria) {
        Specification<Ticket> specs = createSpecs(criteria);
        return specs.and(hasFlightId(flightId));
    }

}
