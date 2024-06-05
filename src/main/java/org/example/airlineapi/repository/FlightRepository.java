package org.example.airlineapi.repository;

import jakarta.persistence.LockModeType;
import org.example.airlineapi.model.flight.Flight;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;

import java.util.List;
import java.util.Optional;

public interface FlightRepository extends JpaRepository<Flight, Long>, JpaSpecificationExecutor<Flight> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Flight> findWithLockById(long id);
}
