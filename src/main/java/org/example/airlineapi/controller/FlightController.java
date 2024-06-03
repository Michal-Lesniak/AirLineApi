package org.example.airlineapi.controller;

import lombok.RequiredArgsConstructor;
import org.example.airlineapi.model.flight.FlightSearchCriteria;
import org.example.airlineapi.model.flight.command.CreateFlightCommand;
import org.example.airlineapi.model.flight.command.UpdateFlightTimeCommand;
import org.example.airlineapi.model.flight.dto.FlightDto;
import org.example.airlineapi.service.FlightService;
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

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/flights")
public class FlightController {

    private final FlightService flightService;

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

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FlightDto create(@RequestBody CreateFlightCommand command){
        return flightService.create(command);
    }

    @PutMapping("/{id}")
    public FlightDto update(@PathVariable long id, @RequestBody CreateFlightCommand command){
        return flightService.update(id, command);
    }

    @PatchMapping("/{id}")
    public FlightDto updateTime(@PathVariable long id, @RequestBody UpdateFlightTimeCommand  command){
        return flightService.updateTime(id, command);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long id){
        flightService.delete(id);
    }

}
