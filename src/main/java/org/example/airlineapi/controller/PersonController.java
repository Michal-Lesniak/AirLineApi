package org.example.airlineapi.controller;

import lombok.RequiredArgsConstructor;
import org.example.airlineapi.model.person.PersonSearchCriteria;
import org.example.airlineapi.model.person.command.CreatePersonCommand;
import org.example.airlineapi.model.person.dto.PersonDto;
import org.example.airlineapi.model.ticket.TicketSearchCriteria;
import org.example.airlineapi.model.ticket.dto.TicketDto;
import org.example.airlineapi.service.PersonService;
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

@RequestMapping("api/v1/persons")
@RestController
@RequiredArgsConstructor
public class PersonController {

    private final PersonService personService;
    private final TicketService ticketService;

    @GetMapping
    public List<PersonDto> getAll(){
        return personService.getAll();
    }

    @GetMapping("/search")
    public Page<PersonDto> search(@PageableDefault Pageable pageable, @RequestBody PersonSearchCriteria criteria){
        return personService.search(pageable, criteria);
    }

    @GetMapping("/{id}")
    public PersonDto getPersonById(@PathVariable long id){
        return personService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PersonDto create(@RequestBody CreatePersonCommand command){
        return personService.create(command);
    }

    @PutMapping("/{id}")
    public PersonDto update(@PathVariable long id, @RequestBody CreatePersonCommand command){
        return personService.update(id, command);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long id){
        personService.delete(id);
    }

    @GetMapping("/{personId}/tickets")
    public Page<TicketDto> getAllByPersonId(@PathVariable("personId") long id, @PageableDefault Pageable pageable, @RequestBody TicketSearchCriteria criteria){
        return ticketService.getAllByPersonId(id, pageable, criteria);
    }

}
