package org.example.airlineapi.service;

import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import org.example.airlineapi.exception.AlreadyHaveTicketException;
import org.example.airlineapi.exception.OverbookingException;
import org.example.airlineapi.exception.NotFoundException;
import org.example.airlineapi.exception.UpdateOptimisticLockingException;
import org.example.airlineapi.mapper.TicketMapper;
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
import org.example.airlineapi.utils.TriFunction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.MessageFormat;
import java.util.function.BiFunction;

import static org.example.airlineapi.mapper.TicketMapper.toDto;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    private final PersonRepository personRepository;
    private final FlightRepository flightRepository;


    private Page<TicketDto> getAll(TriFunction <Long, PageRequest, Specification<Ticket>, Page<Ticket>> repositoryFunction) {
        Specification<Ticket> specs = TicketSpecs.createSpecs(criteria);
        PageRequest pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());

        Page<Ticket> tickets = repositoryFunction.apply(pageRequest, specs);
        return ticketRepository.findAll(specs, pageRequest)
                .map(TicketMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Page<TicketDto> getAllByFlightId(long flightId, Pageable pageable, TicketSearchCriteria criteria) {
        Specification<Ticket> specs = TicketSpecs.createSpecs(criteria);
        PageRequest pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());
        return getAll(() -> ticketRepository.findAllByFlightId(flightId, pageRequest, specs));
    }



    @Transactional(readOnly = true)
    public TicketDto getById(long id) {
        return ticketRepository.findById(id)
                .map(TicketMapper::toDto)
                .orElseThrow(() -> new NotFoundException(MessageFormat
                        .format("Ticket with id {0} not found", id)));
    }

    @Transactional
    public TicketDto create(CreateTicketCommand command) {
        Ticket ticket = TicketMapper.fromCommand(command);

        Flight flight = flightRepository.findWithLockById(command.getFlightId())
                .orElseThrow(() -> new NotFoundException(MessageFormat
                        .format("Flight with id {0} not found", command.getFlightId())));

        if(flight.getTickets().size() >= flight.getAvailableSeats()){
            throw new OverbookingException(MessageFormat
                    .format("Flight with id {0} has no available seats", command.getFlightId()));
        }

        Person person = personRepository.findWithLockById(command.getPersonId())
                .orElseThrow(() -> new NotFoundException(MessageFormat
                        .format("Person with id {0} not found", command.getPersonId())));

        if(!ticketRepository.findByPersonIdAndFlightId(command.getPersonId(), command.getFlightId()).isEmpty()){
            throw new AlreadyHaveTicketException(MessageFormat
                    .format("Person with id {0} already has a ticket for flight with id {1}", command.getPersonId(), command.getFlightId()));
        }

        ticket.setFlight(flight);
        ticket.setPerson(person);

        return toDto(ticketRepository.save(ticket));
    }

    @Transactional
    public TicketDto updatePerson(long flightId, long ticketId, UpdateTicketPersonCommand command) {
        try {
            Ticket ticket = ticketRepository.findById(ticketId)
                    .orElseThrow(() -> new NotFoundException(MessageFormat
                            .format("Ticket with id {0} not found", ticketId)));

            Person person = personRepository.findWithLockById(command.getPersonId())
                    .orElseThrow(() -> new NotFoundException(MessageFormat
                            .format("Person with id {0} not found", command.getPersonId())));

            if(!ticketRepository.findByPersonIdAndFlightId(command.getPersonId(), flightId).isEmpty()){
                throw new AlreadyHaveTicketException(MessageFormat
                        .format("Person with id {0} already has a ticket for flight with id {1}", command.getPersonId(), flightId));
            }

            ticket.setPerson(person);
            return toDto(ticket);
        } catch (OptimisticLockException e) {
            throw new UpdateOptimisticLockingException(MessageFormat
                    .format("Ticket with id {0} was updated by another user. Please send again your request", ticketId));
        }
    }
}