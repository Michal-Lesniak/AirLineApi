package org.example.airlineapi.controller;

import lombok.RequiredArgsConstructor;
import org.example.airlineapi.model.ticket.TicketSearchCriteria;
import org.example.airlineapi.model.ticket.command.CreateTicketCommand;
import org.example.airlineapi.model.ticket.command.UpdateTicketPersonCommand;
import org.example.airlineapi.model.ticket.dto.TicketDto;
import org.example.airlineapi.service.TicketService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/tickets")
public class TicketController {

    private final TicketService ticketService;

    @GetMapping
    public List<TicketDto> getAll(){
        return ticketService.getAll();
    }

    @GetMapping("/search")
    public Page<TicketDto> search(@PageableDefault Pageable pageable, @RequestBody TicketSearchCriteria criteria){
        return ticketService.search(pageable, criteria);
    }

    @GetMapping("/{id}")
    public TicketDto getById(@PathVariable long id){
        return ticketService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TicketDto create(@RequestBody CreateTicketCommand command){
        return ticketService.create(command);
    }

    @PutMapping("/{id}")
    public TicketDto updatePerson(@PathVariable long id, @RequestBody UpdateTicketPersonCommand command){
        return ticketService.updatePerson(id, command);
    }
}
