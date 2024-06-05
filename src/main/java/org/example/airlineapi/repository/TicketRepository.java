package org.example.airlineapi.repository;

import org.example.airlineapi.model.ticket.Ticket;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long>, JpaSpecificationExecutor<Ticket> {
    List<Ticket> findByPersonIdAndFlightId(long personId, long flightId);

    Page<Ticket> findAllByFlightId(long flightId, Pageable pageable, Specification<Ticket> specs);
    Page<Ticket> findAllByPersonId(long personId, Pageable pageable, Specification<Ticket> specs);
}
