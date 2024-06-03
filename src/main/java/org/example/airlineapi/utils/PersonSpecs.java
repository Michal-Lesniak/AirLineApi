package org.example.airlineapi.utils;

import org.example.airlineapi.exception.ArgumentCannotBeNullException;
import org.example.airlineapi.model.person.Person;
import org.example.airlineapi.model.person.PersonSearchCriteria;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class PersonSpecs {
    public static Specification<Person> equalFirstName(String firstName) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("firstName"), firstName);
    }

    public static Specification<Person> equalLastName(String lastName) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("lastName"), lastName);
    }

    public static Specification<Person> equalEmail(String email) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("email"), email);
    }

    public static Specification<Person> equalPhoneNumber(String phoneNumber) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("phoneNumber"), phoneNumber);
    }

    public static Specification<Person> equalDateOfBirth(LocalDate dateOfBirth) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("dateOfBirth"), dateOfBirth);
    }

    public static Specification<Person> createSpecs(PersonSearchCriteria criteria) {
        if (criteria == null) {
            throw new ArgumentCannotBeNullException("Person search criteria cannot be null");
        }

        Specification<Person> specs = Specification.where(null);

        if(criteria.getFirstName() != null && !criteria.getFirstName().isEmpty()) {
            specs = specs.and(equalFirstName(criteria.getFirstName()));
        }

        if(criteria.getLastName() != null && !criteria.getLastName().isEmpty()) {
            specs = specs.and(equalLastName(criteria.getLastName()));
        }

        if(criteria.getEmail() != null && !criteria.getEmail().isEmpty()) {
            specs = specs.and(equalEmail(criteria.getEmail()));
        }

        if(criteria.getPhoneNumber() != null && !criteria.getPhoneNumber().isEmpty()) {
            specs = specs.and(equalPhoneNumber(criteria.getPhoneNumber()));
        }

        if(criteria.getDateOfBirth() != null) {
            specs = specs.and(equalDateOfBirth(criteria.getDateOfBirth()));
        }

        return specs;
    }
}
