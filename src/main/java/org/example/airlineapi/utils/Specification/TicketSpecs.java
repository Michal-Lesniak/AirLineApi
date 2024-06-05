package org.example.airlineapi.utils.Specification;

import org.example.airlineapi.exception.ArgumentCannotBeNullException;
import org.example.airlineapi.model.ticket.Ticket;
import org.example.airlineapi.model.ticket.TicketSearchCriteria;
import org.springframework.data.jpa.domain.Specification;

public class TicketSpecs {

    public static Specification<Ticket> equalSeatNumber(long seatNumber) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("seatNumber"), seatNumber);
    }

    public static Specification<Ticket> equalTicketNumber(long ticketNumber) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("ticketNumber"), ticketNumber);
    }

    public static Specification<Ticket> equalPrice(double price) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("price"), price);
    }

    public static Specification<Ticket> createSpecs(TicketSearchCriteria criteria) {
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
}