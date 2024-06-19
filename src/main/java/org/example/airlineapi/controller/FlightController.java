package org.example.airlineapi.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.airlineapi.model.flight.FlightSearchCriteria;
import org.example.airlineapi.model.flight.command.CreateFlightCommand;
import org.example.airlineapi.model.flight.command.UpdateFlightTimeCommand;
import org.example.airlineapi.model.flight.dto.FlightDto;
import org.example.airlineapi.model.ticket.TicketSearchCriteria;
import org.example.airlineapi.model.ticket.command.CreateTicketCommand;
import org.example.airlineapi.model.ticket.command.UpdateTicketPersonCommand;
import org.example.airlineapi.model.ticket.dto.TicketDto;
import org.example.airlineapi.service.FlightService;
import org.example.airlineapi.service.TicketService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/flights")
public class FlightController {

    private final FlightService flightService;
    private final TicketService ticketService;

    @GetMapping
    public List<FlightDto> getAll(){
        return flightService.getAll();
    }

    @GetMapping("/search")
    public Page<FlightDto> search(@PageableDefault Pageable pageable, @RequestBody FlightSearchCriteria criteria){
        return flightService.search(pageable, criteria);
    }

    @GetMapping("/{id}")
    public FlightDto getFlightById(@PathVariable long id){
        return flightService.getById(id);
    }

    @GetMapping("/{id}/free-seats")
    public Set<Long> getFreeSeats(@PathVariable long id){
        return flightService.getFreeSeat(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FlightDto create(@RequestBody @Valid CreateFlightCommand command){
        return flightService.create(command);
    }

    @PutMapping("/{id}")
    public FlightDto update(@PathVariable long id, @RequestBody @Valid CreateFlightCommand command){
        return flightService.update(id, command);
    }

    @PatchMapping("/{id}")
    public FlightDto updateTime(@PathVariable long id, @RequestBody @Valid UpdateFlightTimeCommand  command){
        return flightService.updateTime(id, command);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long id){
        flightService.delete(id);
    }

    @GetMapping("/{flightId}/tickets")
    public Page<TicketDto> getAllByFlightId(@PathVariable("flightId") long flightId, @PageableDefault Pageable pageable, @RequestBody TicketSearchCriteria criteria){
        return ticketService.getAllByFlightId(flightId, pageable, criteria);
    }

    @PostMapping("/{flightId}/tickets")
    @ResponseStatus(HttpStatus.CREATED)
    public TicketDto create(@PathVariable("flightId") long flightId, @RequestBody @Valid CreateTicketCommand command){
        return ticketService.create(flightId, command);
    }

    @PutMapping("/{flightId}/tickets/{ticketId}")
    public TicketDto updatePerson(@PathVariable("flightId") long flightId, @PathVariable("ticketId") long ticketId, @RequestBody @Valid UpdateTicketPersonCommand command){
        return ticketService.updatePerson(flightId, ticketId, command);
    }

}
