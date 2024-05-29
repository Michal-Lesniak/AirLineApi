package org.example.airlineapi.repository;


import jakarta.persistence.LockModeType;
import org.example.airlineapi.model.person.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;

import java.util.Optional;

public interface PersonRepository extends JpaRepository<Person, Long>, JpaSpecificationExecutor<Person> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Person> findWithLockById(long id);
}
