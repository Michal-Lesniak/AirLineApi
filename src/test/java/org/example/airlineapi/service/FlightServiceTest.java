package org.example.airlineapi.service;

import org.example.airlineapi.exception.ArgumentCannotBeNullException;
import org.example.airlineapi.exception.IllegalBehaviourException;
import org.example.airlineapi.exception.NotFoundException;
import org.example.airlineapi.model.flight.Flight;
import org.example.airlineapi.model.flight.FlightSearchCriteria;
import org.example.airlineapi.model.flight.command.CreateFlightCommand;
import org.example.airlineapi.model.flight.command.UpdateFlightTimeCommand;
import org.example.airlineapi.model.flight.dto.FlightDto;
import org.example.airlineapi.model.ticket.Ticket;
import org.example.airlineapi.repository.FlightRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.example.airlineapi.mapper.FlightMapper.fromCommand;
import static org.example.airlineapi.mapper.FlightMapper.toDto;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FlightServiceTest {

    @Mock
    private FlightRepository flightRepository;

    @InjectMocks
    private FlightService flightService;

    Flight flight1;
    Flight flight2;


    @BeforeEach
    void setUp() {
        flight1 = Flight.builder()
                .id(1L)
                .flightNumber("123")
                .origin("JFK")
                .destination("LAX")
                .departureTime(LocalDateTime.now().plusDays(1))
                .arrivalTime(LocalDateTime.now().plusDays(1).plusHours(5))
                .availableSeats(100)
                .build();

        flight2 = Flight.builder()
                .id(2L)
                .flightNumber("456")
                .origin("LAX")
                .destination("JFK")
                .departureTime(LocalDateTime.now().plusDays(2))
                .arrivalTime(LocalDateTime.now().plusDays(2).plusHours(5))
                .availableSeats(100)
                .build();
    }

    @Test
    void testGetAll_ShouldReturnListOfFlightDtos() {
        List<FlightDto> expectedList = List.of(toDto(flight1), toDto(flight2));

        when(flightRepository.findAll()).thenReturn(List.of(flight1, flight2));
        List<FlightDto> result = flightService.getAll();

        assertEquals(expectedList.size(), result.size());
        assertEquals(expectedList.get(0), result.get(0));
        assertEquals(expectedList.get(1), result.get(1));
        verify(flightRepository).findAll();
        verifyNoMoreInteractions(flightRepository);
    }

    @Test
    void testSearch_ShouldReturnPageOfFlightDtos() {
        Page<FlightDto> expectedPage =  new PageImpl(List.of(toDto(flight1), toDto(flight2)));
        PageRequest pageRequest = PageRequest.of(0, 10);
        FlightSearchCriteria searchCriteria = FlightSearchCriteria.builder().build();
        Specification<Flight> specs = Specification.where(null);

        when(flightRepository.findAll(specs, pageRequest)).thenReturn(new PageImpl(List.of(flight1, flight2)));
        Page<FlightDto> result = flightService.search(pageRequest, searchCriteria);

        assertEquals(expectedPage.getTotalElements(), result.getTotalElements());
        assertEquals(expectedPage.getContent().get(0), result.getContent().get(0));
        assertEquals(expectedPage.getContent().get(1), result.getContent().get(1));
        verify(flightRepository).findAll(specs, pageRequest);
        verifyNoMoreInteractions(flightRepository);
    }

    @Test
    void testSearch_CriteriaIsNull_ThrowArgumentCannotBeNullException(){
        FlightSearchCriteria searchCriteria = null;
        PageRequest pageRequest = PageRequest.of(0, 10);

        assertThatExceptionOfType(ArgumentCannotBeNullException.class)
                .isThrownBy(() -> flightService.search(pageRequest, searchCriteria))
                .withMessage("Flight search criteria cannot be null");
        verifyNoInteractions(flightRepository);
    }

    @Test
    void testGetById_ShouldReturnFlightDto() {
        long id = 1L;
        when(flightRepository.findById(id)).thenReturn(Optional.ofNullable(flight1));
        FlightDto expectedFlight = toDto(flight1);

        FlightDto result = flightService.getById(id);

        assertEquals(expectedFlight, result);
        verify(flightRepository).findById(id);
        verifyNoMoreInteractions(flightRepository);
    }

    @Test
    void testGetById_FlightNotFound_ThrowNotFoundException() {
        long id = 3L;
        when(flightRepository.findById(id)).thenReturn(Optional.empty());

        assertThatExceptionOfType(NotFoundException.class)
                .isThrownBy(() -> flightService.getById(id))
                .withMessage("Flight with id 3 not found");
        verify(flightRepository).findById(id);
        verifyNoMoreInteractions(flightRepository);
    }

    @Test
    void testCreate_ShouldCreateFlight() {
        CreateFlightCommand command = createFlightCommand();
        FlightDto expectedFlightDto = toDto(flight1);

        when(flightRepository.save(any(Flight.class))).thenReturn(flight1);
        FlightDto result = flightService.create(command);

        assertEquals(expectedFlightDto, result);
        verify(flightRepository).save(any(Flight.class));
        verifyNoMoreInteractions(flightRepository);
    }

    @Test
    void testUpdate_ShouldUpdateFlight() {
        long id = 1L;
        flight1.setTickets(Set.of());

        CreateFlightCommand command = createFlightCommand();
        Flight updatedFlight = fromCommand(command);
        updatedFlight.setId(id);
        FlightDto expectedFlightDto = toDto(updatedFlight);

        when(flightRepository.findById(id)).thenReturn(Optional.ofNullable(flight1));
        FlightDto result = flightService.update(id, command);

        assertEquals(expectedFlightDto, result);
        verify(flightRepository).findById(id);
        verifyNoMoreInteractions(flightRepository);
    }

    @Test
    void testUpdate_FlightNotFound_ThrowNotFoundException() {
        long id = 3L;
        CreateFlightCommand command = createFlightCommand();

        when(flightRepository.findById(id)).thenReturn(Optional.empty());

        assertThatExceptionOfType(NotFoundException.class)
                .isThrownBy(() -> flightService.update(id, command))
                .withMessage("Flight with id 3 not found");
        verify(flightRepository).findById(id);
        verifyNoMoreInteractions(flightRepository);
    }

    @Test
    void testUpdate_FlightHasTickets_ThrowIllegalBehaviourException() {
        long id = 1L;
        Ticket ticket = Ticket.builder()
                .id(1L)
                .ticketNumber(1)
                .build();
        flight1.setTickets(Set.of(ticket));
        CreateFlightCommand command = createFlightCommand();

        when(flightRepository.findById(id)).thenReturn(Optional.ofNullable(flight1));

        assertThatExceptionOfType(IllegalBehaviourException.class)
                .isThrownBy(() -> flightService.update(id, command))
                .withMessage("Flight with id 1 has tickets. Cannot update flight with sold ticket.");
        verify(flightRepository).findById(id);
        verifyNoMoreInteractions(flightRepository);
    }

    @Test
    void testUpdateTime_ShouldUpdateFlightTime() {
        long id = 1L;
        UpdateFlightTimeCommand command = new UpdateFlightTimeCommand(
                LocalDateTime.now().plusDays(4),
                LocalDateTime.now().plusDays(5).plusHours(5)
        );
        Flight updatedFlight = flight1;
        updatedFlight.setDepartureTime(command.getDepartureTime());
        updatedFlight.setArrivalTime(command.getArrivalTime());
        FlightDto expectedFlightDto = toDto(updatedFlight);

        when(flightRepository.findById(id)).thenReturn(Optional.ofNullable(flight1));
        FlightDto result = flightService.updateTime(id, command);

        assertEquals(expectedFlightDto, result);
        verify(flightRepository).findById(id);
        verifyNoMoreInteractions(flightRepository);
    }

    @Test
    void testUpdateTime_FlightNotFound_ThrowNotFoundException() {
        long id = 3L;
        UpdateFlightTimeCommand command = new UpdateFlightTimeCommand(
                LocalDateTime.now().plusDays(4),
                LocalDateTime.now().plusDays(5).plusHours(5)
        );

        when(flightRepository.findById(id)).thenReturn(Optional.empty());

        assertThatExceptionOfType(NotFoundException.class)
                .isThrownBy(() -> flightService.updateTime(id, command))
                .withMessage("Flight with id 3 not found");
        verify(flightRepository).findById(id);
        verifyNoMoreInteractions(flightRepository);
    }

    @Test
    void testDelete_ShouldDeleteFlight() {
        long id = 1L;
        when(flightRepository.findById(id)).thenReturn(Optional.ofNullable(flight1));

        flightService.delete(id);

        verify(flightRepository).findById(id);
        verify(flightRepository).delete(flight1);
        verifyNoMoreInteractions(flightRepository);
    }

    @Test
    void testDelete_FlightNotFound_ThrowNotFoundException() {
        long id = 3L;
        when(flightRepository.findById(id)).thenReturn(Optional.empty());

        assertThatExceptionOfType(NotFoundException.class)
                .isThrownBy(() -> flightService.delete(id))
                .withMessage("Flight with id 3 not found");
        verify(flightRepository).findById(id);
        verifyNoMoreInteractions(flightRepository);
    }

    CreateFlightCommand createFlightCommand(){
        return new CreateFlightCommand(
                "999",
                "JAK",
                "TAK",
                LocalDateTime.now().plusDays(10),
                LocalDateTime.now().plusDays(11).plusHours(5),
                75
        );
    }
}