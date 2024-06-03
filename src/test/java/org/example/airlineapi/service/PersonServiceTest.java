package org.example.airlineapi.service;

import org.example.airlineapi.exception.ArgumentCannotBeNullException;
import org.example.airlineapi.exception.NotFoundException;
import org.example.airlineapi.model.person.Person;
import org.example.airlineapi.model.person.PersonSearchCriteria;
import org.example.airlineapi.model.person.command.CreatePersonCommand;
import org.example.airlineapi.model.person.dto.PersonDto;
import org.example.airlineapi.repository.PersonRepository;
import org.example.airlineapi.utils.PersonSpecs;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.example.airlineapi.mapper.PersonMapper.fromCommand;
import static org.example.airlineapi.mapper.PersonMapper.toDto;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyShort;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PersonServiceTest {

    @Mock
    private PersonRepository personRepository;

    @InjectMocks
    private PersonService personService;

    Person person1;
    Person person2;

    @BeforeEach
    void setUp() {
        person1 = Person.builder()
                .id(1L)
                .firstName("Alice")
                .lastName("Doe")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .email("fas@wp.pl")
                .phoneNumber("999456789")
                .build();

        person2 = Person.builder()
                .id(2L)
                .firstName("Bob")
                .lastName("Smith")
                .dateOfBirth(LocalDate.of(1995, 2, 2))
                .email("person2@wp.pl")
                .phoneNumber("432324534")
                .build();
    }

    @Test
    void testGetAll_ShouldReturnListOfPersonDtos() {
        List<PersonDto> expectedList = List.of(toDto(person1), toDto(person2));

        when(personRepository.findAll()).thenReturn(List.of(person1, person2));
        List<PersonDto> result = personService.getAll();

        assertEquals(expectedList.size(), result.size());
        assertEquals(expectedList.get(0), result.get(0));
        assertEquals(expectedList.get(1), result.get(1));
        verify(personRepository).findAll();
        verifyNoMoreInteractions(personRepository);
    }

    @Test
    void testSearch_ShouldReturnPageOfPersonDtos() {
        PageRequest pageRequest = PageRequest.of(0, 2);
        PersonSearchCriteria searchCriteria = PersonSearchCriteria.builder().build();
        Page<PersonDto> expectedPage = new PageImpl<>(List.of(toDto(person1), toDto(person2)));
        Specification<Person> specs = PersonSpecs.createSpecs(searchCriteria);


        when(personRepository.findAll(specs, pageRequest)).thenReturn(new PageImpl<>(List.of(person1, person2)));
        Page<PersonDto> result = personService.search(pageRequest, searchCriteria);

        assertEquals(expectedPage.getTotalElements(), result.getTotalElements());
        assertEquals(expectedPage.getContent().get(0), result.getContent().get(0));
        assertEquals(expectedPage.getContent().get(1), result.getContent().get(1));
        verify(personRepository).findAll(specs, pageRequest);
        verifyNoMoreInteractions(personRepository);
    }

    @Test
    void testSearch_CriteriaIsNull_throwArgumentNullException() {
        PageRequest pageRequest = PageRequest.of(0, 2);
        PersonSearchCriteria searchCriteria = null;

        assertThatExceptionOfType(ArgumentCannotBeNullException.class)
                .isThrownBy(() -> personService.search(pageRequest, searchCriteria))
                .withMessage("Person search criteria cannot be null");
        verifyNoInteractions(personRepository);
    }

    @Test
    void testGetById_ShouldReturnPersonDto() {
        long id = 1L;
        when(personRepository.findById(id)).thenReturn(Optional.ofNullable(person1));
        PersonDto result = personService.getById(1L);

        assertEquals(toDto(person1), result);
        verify(personRepository).findById(1L);
        verifyNoMoreInteractions(personRepository);
    }

    @Test
    void testGetById_PersonNotFound_ShouldThrowNotFoundException() {
        long id = 3L;
        when(personRepository.findById(id)).thenReturn(Optional.empty());

        assertThatExceptionOfType(NotFoundException.class)
                .isThrownBy(() -> personService.getById(3L))
                .withMessage("Person with id 3 not found");
        verify(personRepository).findById(id);
        verifyNoMoreInteractions(personRepository);
    }

    @Test
    void testCreate_ShouldCreatePerson() {
        CreatePersonCommand command = createPersonCommand();
        Person person = fromCommand(command);
        person.setId(1L);
        PersonDto expectedDto = toDto(person);

        when(personRepository.save(any(Person.class))).thenReturn(person);
        PersonDto result = personService.create(command);

        assertEquals(expectedDto, result);
        verify(personRepository).save(any(Person.class));
        verifyNoMoreInteractions(personRepository);
    }

    @Test
    void testUpdate_ShouldUpdatePerson() {
        long id = 1L;
        CreatePersonCommand command = createPersonCommand();
        Person person = fromCommand(command);
        person.setId(id);
        PersonDto expectedDto = toDto(person);

        when(personRepository.findById(id)).thenReturn(Optional.ofNullable(person));
        PersonDto result = personService.update(id, command);

        assertEquals(expectedDto, result);
        verify(personRepository).findById(id);
        verifyNoMoreInteractions(personRepository);
    }

    @Test
    void testUpdate_PersonNotFound_ShouldThrowNotFoundException() {
        long id = 3L;
        CreatePersonCommand command = createPersonCommand();

        when(personRepository.findById(id)).thenReturn(Optional.empty());

        assertThatExceptionOfType(NotFoundException.class)
                .isThrownBy(() -> personService.update(id, command))
                .withMessage("Person with id 3 not found");
        verify(personRepository).findById(id);
        verifyNoMoreInteractions(personRepository);
    }

    //TODO optimistic locking test

    @Test
    void testDelete_ShouldDeletePerson() {
        long id = 1L;
        when(personRepository.findById(id)).thenReturn(Optional.ofNullable(person1));

        personService.delete(id);

        verify(personRepository).findById(id);
        verify(personRepository).delete(person1);
        verifyNoMoreInteractions(personRepository);
    }

    @Test
    void testDelete_PersonNotFound_ShouldThrowNotFoundException() {
        long id = 3L;
        when(personRepository.findById(id)).thenReturn(Optional.empty());

        assertThatExceptionOfType(NotFoundException.class)
                .isThrownBy(() -> personService.delete(id))
                .withMessage("Person with id 3 not found");
        verify(personRepository).findById(id);
        verifyNoMoreInteractions(personRepository);
    }

    CreatePersonCommand createPersonCommand(){
        return CreatePersonCommand.builder()
                .firstName("Jan")
                .lastName("Kowalski")
                .dateOfBirth(LocalDate.of(1994, 1, 1))
                .email("dsakowal@das.pl")
                .phoneNumber("124322323")
                .build();
    }
}