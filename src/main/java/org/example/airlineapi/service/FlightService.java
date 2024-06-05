package org.example.airlineapi.service;

import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import org.example.airlineapi.exception.DeleteOptimisticLockingException;
import org.example.airlineapi.exception.IllegalBehaviourException;
import org.example.airlineapi.exception.NotFoundException;
import org.example.airlineapi.exception.UpdateOptimisticLockingException;
import org.example.airlineapi.mapper.FlightMapper;
import org.example.airlineapi.model.flight.Flight;
import org.example.airlineapi.model.flight.FlightSearchCriteria;
import org.example.airlineapi.model.flight.command.CreateFlightCommand;
import org.example.airlineapi.model.flight.command.UpdateFlightTimeCommand;
import org.example.airlineapi.model.flight.dto.FlightDto;
import org.example.airlineapi.repository.FlightRepository;
import org.example.airlineapi.utils.Specification.FlightSpecs;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.MessageFormat;
import java.util.List;

import static org.example.airlineapi.mapper.FlightMapper.fromCommand;
import static org.example.airlineapi.mapper.FlightMapper.toDto;

@Service
@RequiredArgsConstructor
public class FlightService {

    private final FlightRepository flightRepository;

    @Transactional(readOnly = true)
    public List<FlightDto> getAll() {
        return flightRepository.findAll()
                .stream()
                .map(FlightMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<FlightDto> search(Pageable pageable, FlightSearchCriteria criteria) {
        Specification<Flight> specs = FlightSpecs.createSpecs(criteria);
        PageRequest pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());
        return flightRepository.findAll(specs, pageRequest)
                .map(FlightMapper::toDto);
    }

    @Transactional(readOnly = true)
    public FlightDto getById(long id) {
        return flightRepository.findById(id)
                .map(FlightMapper::toDto)
                .orElseThrow(() -> new NotFoundException(MessageFormat
                        .format("Flight with id {0} not found", id)));
    }

    @Transactional
    public FlightDto create(CreateFlightCommand command) {
        Flight flight = fromCommand(command);
        return toDto(flightRepository.save(flight));
    }

    @Transactional
    public FlightDto update(long id, CreateFlightCommand command) {
        try {
            Flight flight = flightRepository.findById(id)
                    .orElseThrow(() -> new NotFoundException(MessageFormat
                            .format("Flight with id {0} not found", id)));

            if(!flight.getTickets().isEmpty()){
                throw new IllegalBehaviourException(MessageFormat
                        .format("Flight with id {0} has tickets. Cannot update flight with sold ticket.", id));
            }

            flight.setFlightNumber(command.flightNumber());
            flight.setOrigin(command.origin());
            flight.setDestination(command.destination());
            flight.setDepartureTime(command.departureTime());
            flight.setArrivalTime(command.arrivalTime());
            flight.setAvailableSeats(command.availableSeats());
            return toDto(flight);
        } catch (OptimisticLockException e) {
            throw new UpdateOptimisticLockingException(MessageFormat
                    .format("Flight with id {0} was updated by another user. Please send again your request", id));
        }
    }

    @Transactional
    public FlightDto updateTime(long id, UpdateFlightTimeCommand command){
        Flight flight = flightRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(MessageFormat
                        .format("Flight with id {0} not found", id)));

        flight.setDepartureTime(command.getDepartureTime());
        flight.setArrivalTime(command.getArrivalTime());

        //TODO send a message to the customers that the flight time was updated

        return toDto(flight);
    }

    @Transactional
    public void delete(long id) {
        try {
            Flight flight = flightRepository.findById(id)
                    .orElseThrow(() -> new NotFoundException(MessageFormat
                            .format("Flight with id {0} not found", id)));

            //TODO send a message to the customers that the flight was canceled

            flightRepository.delete(flight);
        } catch (OptimisticLockException e) {
            throw new DeleteOptimisticLockingException(MessageFormat
                    .format("Flight with id {0} was updated by another user. Please send again your request", id));
        }
    }
}
