package org.example.airlineapi.service;

import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import org.example.airlineapi.exception.DeleteOptimisticLockingException;
import org.example.airlineapi.exception.IllegalBehaviourException;
import org.example.airlineapi.exception.NotFoundException;
import org.example.airlineapi.exception.UpdateOptimisticLockingException;
import org.example.airlineapi.mapper.PersonMapper;
import org.example.airlineapi.model.person.Person;
import org.example.airlineapi.model.person.PersonSearchCriteria;
import org.example.airlineapi.model.person.command.CreatePersonCommand;
import org.example.airlineapi.model.person.dto.PersonDto;
import org.example.airlineapi.repository.PersonRepository;
import org.example.airlineapi.utils.Specification.PersonSpecs;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.MessageFormat;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PersonService {
    private final PersonRepository personRepository;

    @Transactional(readOnly = true)
    public List<PersonDto> getAll() {
        return personRepository.findAll()
                .stream()
                .map(PersonMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<PersonDto> search(Pageable pageable, PersonSearchCriteria criteria) {
        Specification<Person> specs = PersonSpecs.createSpecs(criteria);
        PageRequest pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());

        return personRepository.findAll(specs, pageRequest)
                .map(PersonMapper::toDto);
    }

    @Transactional(readOnly = true)
    public PersonDto getById(long id) {
        return personRepository.findById(id)
                .map(PersonMapper::toDto)
                .orElseThrow(() -> new NotFoundException(MessageFormat
                        .format("Person with id {0} not found", id)));
    }

    @Transactional
    public PersonDto create(CreatePersonCommand command) {
        Person person = Person.builder()
                .firstName(command.getFirstName())
                .lastName(command.getLastName())
                .email(command.getEmail())
                .phoneNumber(command.getPhoneNumber())
                .dateOfBirth(command.getDateOfBirth())
                .build();

        return PersonMapper.toDto(personRepository.save(person));
    }

    @Transactional
    public PersonDto update(long id, CreatePersonCommand command) {
        try {
            Person person = personRepository.findById(id)
                    .orElseThrow(() -> new NotFoundException("Person with id " + id + " not found"));
            person.setFirstName(command.getFirstName());
            person.setLastName(command.getLastName());
            person.setEmail(command.getEmail());
            person.setPhoneNumber(command.getPhoneNumber());
            person.setDateOfBirth(command.getDateOfBirth());
            return PersonMapper.toDto(person);
        }catch (OptimisticLockException e){
            throw new UpdateOptimisticLockingException(MessageFormat
                    .format("Person with id {0} was updated by another user. Please refresh and try again.", id));
        }
    }

    @Transactional
    public void delete(long id) {
        Person person = personRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Person with id " + id + " not found"));

        if(person.getTicket() != null && !person.getTicket().isEmpty()){
            throw new IllegalBehaviourException(MessageFormat
                    .format("Person with id {0} has tickets. Please delete tickets first.", id));
        }

        try {
            personRepository.delete(person);
        } catch (OptimisticLockException e){
            throw new DeleteOptimisticLockingException(MessageFormat
                    .format("Person with id {0} was updated by another user. Please refresh and try again.", id));
        }
    }
}
