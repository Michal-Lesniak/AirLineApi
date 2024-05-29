package org.example.airlineapi.mapper;

import org.example.airlineapi.model.person.Person;
import org.example.airlineapi.model.person.command.CreatePersonCommand;
import org.example.airlineapi.model.person.dto.PersonDto;
import org.springframework.stereotype.Component;

@Component
public class PersonMapper {

    public static Person fromCommand(CreatePersonCommand command) {
        return Person.builder()
                .firstName(command.getFirstName())
                .lastName(command.getLastName())
                .email(command.getEmail())
                .phoneNumber(command.getPhoneNumber())
                .dateOfBirth(command.getDateOfBirth())
                .build();
    }

    public static PersonDto toDto(Person person) {
        return PersonDto.builder()
                .id(person.getId())
                .firstName(person.getFirstName())
                .lastName(person.getLastName())
                .email(person.getEmail())
                .phoneNumber(person.getPhoneNumber())
                .dateOfBirth(person.getDateOfBirth())
                .build();
    }
}
