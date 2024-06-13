package org.example.airlineapi.service;

import jakarta.persistence.OptimisticLockException;
import org.example.airlineapi.exception.AlreadyHaveTicketException;
import org.example.airlineapi.exception.OverbookingException;
import org.example.airlineapi.exception.NotFoundException;
import org.example.airlineapi.exception.UpdateOptimisticLockingException;
import org.example.airlineapi.model.flight.Flight;
import org.example.airlineapi.model.person.Person;
import org.example.airlineapi.model.ticket.Ticket;
import org.example.airlineapi.model.ticket.TicketSearchCriteria;
import org.example.airlineapi.model.ticket.command.CreateTicketCommand;
import org.example.airlineapi.model.ticket.command.UpdateTicketPersonCommand;
import org.example.airlineapi.model.ticket.dto.TicketDto;
import org.example.airlineapi.repository.FlightRepository;
import org.example.airlineapi.repository.PersonRepository;
import org.example.airlineapi.repository.TicketRepository;
import org.example.airlineapi.utils.Specification.TicketSpecs;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.example.airlineapi.mapper.TicketMapper.fromCommand;
import static org.example.airlineapi.mapper.TicketMapper.toDto;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TicketServiceTest {
    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private PersonRepository personRepository;

    @Mock
    private FlightRepository flightRepository;

    @InjectMocks
    private TicketService ticketService;

    @Captor
    private ArgumentCaptor<Specification> specsCaptor;

    @Captor
    private ArgumentCaptor<Pageable> pageableCaptor;


    Ticket ticket1;
    Ticket ticket2;
    Flight flight;
    Person person;

    @BeforeEach
    void setUp() {
        person = Person.builder()
                .id(1L)
                .firstName("Alice")
                .lastName("Doe")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .email("fas@wp.pl")
                .phoneNumber("999456789")
                .build();

        flight = Flight.builder()
                .id(1L)
                .flightNumber("AA123")
                .origin("NYC")
                .destination("LAX")
                .departureTime(LocalDateTime.of(2024, 1, 1, 10, 0))
                .arrivalTime(LocalDateTime.of(2024, 1, 1, 14, 0))
                .availableSeats(100)
                .build();

        ticket1 = Ticket.builder()
                .id(1L)
                .flight(flight)
                .person(person)
                .build();

        ticket2 = Ticket.builder()
                .id(2L)
                .flight(flight)
                .person(person)
                .build();
    }

    @Test
    void testGetAllByFlightId_ShouldReturnListOfTicketDtos() {
        long flightId = 1L;
        Pageable pageRequest = PageRequest.of(0, 2);
        TicketSearchCriteria searchCriteria = TicketSearchCriteria.builder().build();
        Page<TicketDto> expectedPage = new PageImpl<>(List.of(toDto(ticket1), toDto(ticket2)));

        when(ticketRepository.findAll(specsCaptor.capture(), pageableCaptor.capture())).thenReturn(new PageImpl(List.of(ticket1, ticket2)));
        Page<TicketDto> result = ticketService.getAllByFlightId(flightId, pageRequest, searchCriteria);

        assertEquals(expectedPage.getTotalElements(), result.getTotalElements());
        assertEquals(expectedPage.getContent().get(0), result.getContent().get(0));
        assertEquals(expectedPage.getContent().get(1), result.getContent().get(1));
        verify(ticketRepository).findAll(specsCaptor.getValue(), pageableCaptor.getValue());
        verifyNoMoreInteractions(ticketRepository);
    }

    @Test
    void testGetAllByPersonId_ShouldReturnListOfTicketDtos() {
        long personId = 1L;
        PageRequest pageRequest = PageRequest.of(0, 2);
        TicketSearchCriteria searchCriteria = TicketSearchCriteria.builder().build();
        Page<TicketDto> expectedPage = new PageImpl<>(List.of(toDto(ticket1), toDto(ticket2)));

        when(ticketRepository.findAll(specsCaptor.capture(), pageableCaptor.capture())).thenReturn(new PageImpl(List.of(ticket1, ticket2)));
        Page<TicketDto> result = ticketService.getAllByPersonId(personId, pageRequest, searchCriteria);

        assertEquals(expectedPage.getTotalElements(), result.getTotalElements());
        assertEquals(expectedPage.getContent().get(0), result.getContent().get(0));
        assertEquals(expectedPage.getContent().get(1), result.getContent().get(1));
        verify(ticketRepository).findAll(specsCaptor.getValue(), pageableCaptor.getValue());
        verifyNoMoreInteractions(ticketRepository);
    }

    @Test
    void testGetById_ShouldReturnTicketDto() {
        long id = 1L;
        when(ticketRepository.findById(id)).thenReturn(Optional.ofNullable(ticket1));
        TicketDto result = ticketService.getById(1L);

        assertEquals(toDto(ticket1), result);
        verify(ticketRepository).findById(1L);
        verifyNoMoreInteractions(ticketRepository);
    }

    @Test
    void testGetById_TicketNotFound_ShouldThrowNotFoundException() {
        long id = 3L;
        when(ticketRepository.findById(id)).thenReturn(Optional.empty());

        assertThatExceptionOfType(NotFoundException.class)
                .isThrownBy(() -> ticketService.getById(3L))
                .withMessage("Ticket with id 3 not found");
        verify(ticketRepository).findById(3L);
        verifyNoMoreInteractions(ticketRepository);
    }

    @Test
    void testCreate_ShouldCreateTicket() {
        long flightId = 1L;
        CreateTicketCommand command = createTicketCommand();
        Ticket ticket = fromCommand(command);
        flight.setTickets(Set.of());
        ticket.setFlight(flight);
        ticket.setPerson(person);
        TicketDto expectedDto = toDto(ticket);

        when(flightRepository.findWithLockById(flightId)).thenReturn(Optional.ofNullable(flight));
        when(personRepository.findWithLockById(command.getPersonId())).thenReturn(Optional.ofNullable(person));
        when(ticketRepository.findByPersonIdAndFlightId(command.getPersonId(), flightId)).thenReturn(List.of());
        when(ticketRepository.save(any(Ticket.class))).thenReturn(ticket);

        TicketDto result = ticketService.create(flightId, command);

        assertEquals(expectedDto, result);
        verify(flightRepository).findWithLockById(flightId);
        verify(personRepository).findWithLockById(command.getPersonId());
        verify(ticketRepository).findByPersonIdAndFlightId(command.getPersonId(), flightId);
        verify(ticketRepository).save(any(Ticket.class));
        verifyNoMoreInteractions(ticketRepository, flightRepository, personRepository);
    }

    @Test
    void testCreate_FlightNotFound_ShouldThrowNotFoundException() {
        long flightId = 3L;
        CreateTicketCommand command = createTicketCommand();

        when(flightRepository.findWithLockById(flightId)).thenReturn(Optional.empty());

        assertThatExceptionOfType(NotFoundException.class)
                .isThrownBy(() -> ticketService.create(flightId, command))
                .withMessage("Flight with id " + flightId + " not found");
        verify(flightRepository).findWithLockById(flightId);
        verifyNoMoreInteractions(flightRepository, personRepository, ticketRepository);
    }

    @Test
    void testCreate_PersonNotFound_ShouldThrowNotFoundException() {
        long flightId = 1L;
        CreateTicketCommand command = createTicketCommand();
        flight.setTickets(Set.of());

        when(flightRepository.findWithLockById(flightId)).thenReturn(Optional.ofNullable(flight));
        when(personRepository.findWithLockById(command.getPersonId())).thenReturn(Optional.empty());

        assertThatExceptionOfType(NotFoundException.class)
                .isThrownBy(() -> ticketService.create(flightId, command))
                .withMessage("Person with id " + command.getPersonId() + " not found");
        verify(flightRepository).findWithLockById(flightId);
        verify(personRepository).findWithLockById(command.getPersonId());
        verifyNoMoreInteractions(flightRepository, personRepository, ticketRepository);
    }

    @Test
    void testCreate_NoAvailableSeats_ShouldThrowLackOfSeatException() {
        long flightId = 1L;
        CreateTicketCommand command = createTicketCommand();
        flight.setTickets(Set.of(ticket2));
        flight.setAvailableSeats(0);

        when(flightRepository.findWithLockById(flightId)).thenReturn(Optional.of(flight));

        assertThatExceptionOfType(OverbookingException.class)
                .isThrownBy(() -> ticketService.create(flightId, command))
                .withMessage("Flight with id " + flightId + " has no available seats");
        verify(flightRepository).findWithLockById(flightId);
        verifyNoMoreInteractions(flightRepository, personRepository, ticketRepository);
    }

    @Test
    void testCreate_PersonAlreadyHasTicket_ShouldThrowNotFoundException() {
        long flightId = 1L;
        Ticket tempTicket = Ticket.builder()
                .id(3L)
                .flight(flight)
                .person(person)
                .build();
        flight.setTickets(Set.of());
        CreateTicketCommand command = createTicketCommand();

        when(flightRepository.findWithLockById(flightId)).thenReturn(Optional.of(flight));
        when(personRepository.findWithLockById(command.getPersonId())).thenReturn(Optional.of(person));
        when(ticketRepository.findByPersonIdAndFlightId(command.getPersonId(), flightId)).thenReturn(List.of(tempTicket));

        assertThatExceptionOfType(AlreadyHaveTicketException.class)
                .isThrownBy(() -> ticketService.create(flightId, command))
                .withMessage("Person with id " + command.getPersonId() + " already has a ticket for flight with id " + flightId);
        verify(flightRepository).findWithLockById(flightId);
        verify(personRepository).findWithLockById(command.getPersonId());
        verify(ticketRepository).findByPersonIdAndFlightId(command.getPersonId(), flightId);
        verifyNoMoreInteractions(flightRepository, personRepository, ticketRepository);
    }

    @Test
    void testUpdatePerson_ShouldUpdateTicketPerson() {
        long ticketId = 1L;
        long flightId = 1L;
        UpdateTicketPersonCommand command = updateTicketPersonCommand();
        Ticket ticket = ticket1;
        ticket.setFlight(flight);
        ticket.setPerson(person);
        TicketDto expectedDto = toDto(ticket);

        when(ticketRepository.findById(ticketId)).thenReturn(Optional.ofNullable(ticket));
        when(personRepository.findWithLockById(command.getPersonId())).thenReturn(Optional.ofNullable(person));
        when(ticketRepository.findByPersonIdAndFlightId(command.getPersonId(), ticket.getFlight().getId())).thenReturn(List.of());

        TicketDto result = ticketService.updatePerson(flightId, ticketId, command);

        assertEquals(expectedDto, result);
        verify(ticketRepository).findById(ticketId);
        verify(personRepository).findWithLockById(command.getPersonId());
        verifyNoMoreInteractions(ticketRepository, personRepository);
    }

    @Test
    void testUpdatePerson_NewPersonNotFound_ShouldThrowNotFoundException() {
        long ticketId = 1L;
        long flightId = 1L;
        UpdateTicketPersonCommand command = updateTicketPersonCommand();

        when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticket1));
        when(personRepository.findWithLockById(command.getPersonId())).thenReturn(Optional.empty());

        assertThatExceptionOfType(NotFoundException.class)
                .isThrownBy(() -> ticketService.updatePerson(flightId, ticketId, command))
                .withMessage("Person with id " + command.getPersonId() + " not found");
        verify(ticketRepository).findById(ticketId);
        verify(personRepository).findWithLockById(command.getPersonId());
        verifyNoMoreInteractions(ticketRepository, personRepository);
    }

    @Test
    void testUpdatePerson_PersonAlreadyHasTicket_ShouldThrowNotFoundException() {
        long ticketId = 1L;
        long flightId = 1L;
        Ticket tempTicket = Ticket.builder()
                .id(3L)
                .build();
        UpdateTicketPersonCommand command = updateTicketPersonCommand();

        when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticket1));
        when(personRepository.findWithLockById(command.getPersonId())).thenReturn(Optional.of(person));
        when(ticketRepository.findByPersonIdAndFlightId(command.getPersonId(), flight.getId())).thenReturn(List.of(tempTicket));

        assertThatExceptionOfType(AlreadyHaveTicketException.class)
                .isThrownBy(() -> ticketService.updatePerson(flightId, ticketId, command))
                .withMessage("Person with id " + command.getPersonId() + " already has a ticket for flight with id " + flight.getId());
        verify(ticketRepository).findById(ticketId);
        verify(personRepository).findWithLockById(command.getPersonId());
        verify(ticketRepository).findByPersonIdAndFlightId(command.getPersonId(), flight.getId());
        verifyNoMoreInteractions(ticketRepository, personRepository);
    }


    @Test
    void testUpdatePerson_TicketNotFound_ShouldThrowNotFoundException() {
        long flightId = 1L;
        long ticketId = 3L;
        UpdateTicketPersonCommand command = updateTicketPersonCommand();

        when(ticketRepository.findById(ticketId)).thenReturn(Optional.empty());

        assertThatExceptionOfType(NotFoundException.class)
                .isThrownBy(() -> ticketService.updatePerson(flightId, ticketId, command))
                .withMessage("Ticket with id 3 not found");
        verify(ticketRepository).findById(ticketId);
        verifyNoMoreInteractions(ticketRepository);
    }

    @Test
    void testUpdatePerson_OptimisticLockException_ShouldThrowUpdateOptimisticLockingException() {
        long flightId = 1L;
        long ticketId = 1L;
        UpdateTicketPersonCommand command = updateTicketPersonCommand();

        when(ticketRepository.findById(ticketId)).thenReturn(Optional.ofNullable(ticket1));
        when(personRepository.findWithLockById(command.getPersonId())).thenThrow(new OptimisticLockException());

        assertThatExceptionOfType(UpdateOptimisticLockingException.class)
                .isThrownBy(() -> ticketService.updatePerson(flightId, ticketId, command))
                .withMessage("Ticket with id 1 was updated by another user. Please send again your request");

        verify(ticketRepository).findById(ticketId);
        verify(personRepository).findWithLockById(command.getPersonId());
        verifyNoMoreInteractions(ticketRepository, personRepository);
    }

    CreateTicketCommand createTicketCommand() {
        return CreateTicketCommand.builder()
                .personId(1L)
                .build();
    }

    UpdateTicketPersonCommand updateTicketPersonCommand() {
        return UpdateTicketPersonCommand.builder()
                .personId(2L)
                .build();
    }

}