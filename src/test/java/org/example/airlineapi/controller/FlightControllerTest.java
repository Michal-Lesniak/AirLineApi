package org.example.airlineapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.airlineapi.model.flight.FlightSearchCriteria;
import org.example.airlineapi.model.flight.command.CreateFlightCommand;
import org.example.airlineapi.model.flight.command.UpdateFlightTimeCommand;
import org.example.airlineapi.model.ticket.TicketSearchCriteria;
import org.example.airlineapi.model.ticket.command.CreateTicketCommand;
import org.example.airlineapi.model.ticket.command.UpdateTicketPersonCommand;
import org.example.airlineapi.repository.FlightRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Sql(scripts = "/schema.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Sql(scripts = "/init_data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/remove_data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class FlightControllerTest {
    private final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FlightRepository flightRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getAll_ShouldReturnListOfFlights() throws Exception {
        mockMvc.perform(get("/api/v1/flights"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(3)));
    }

    @Test
    void search_ShouldReturnPageOfFlights() throws Exception {
        FlightSearchCriteria criteria = FlightSearchCriteria.builder().build();
        mockMvc.perform(get("/api/v1/flights/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(criteria))
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(3)));
    }

    @Test
    void getFlightById_ShouldReturnFlight() throws Exception {
        mockMvc.perform(get("/api/v1/flights/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flightNumber").value("AA123"));
    }

    @Test
    void getFlightById_NotFound_ShouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/flights/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getFreeSeats_ShouldReturnListOfFreeSeats() throws Exception {
        mockMvc.perform(get("/api/v1/flights/1/free-seats"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(148)));
    }


    @Test
    void create_ShouldCreateFlight() throws Exception {
        CreateFlightCommand command = new CreateFlightCommand(
                "DD123", "Boston", "Houston",
                LocalDateTime.of(2024, 12, 4, 10, 0),
                LocalDateTime.of(2024, 12, 4, 14, 0),
                120
        );

        mockMvc.perform(post("/api/v1/flights")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.flightNumber").value("DD123"));
    }

    @Test
    void create_InvalidCommand_ShouldReturnBadRequest() throws Exception {
        CreateFlightCommand command = new CreateFlightCommand(
                "", "", "",
                null, null,
                0
        );

        mockMvc.perform(post("/api/v1/flights")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void update_ShouldUpdateFlight() throws Exception {
        CreateFlightCommand command = new CreateFlightCommand(
                "AA123", "Boston", "Houston",
                LocalDateTime.of(2025, 6, 4, 10, 0),
                LocalDateTime.of(2025, 6, 4, 14, 0),
                120
        );

        mockMvc.perform(put("/api/v1/flights/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.origin").value("Boston"))
                .andExpect(jsonPath("$.destination").value("Houston"));
    }

    @Test
    void update_FlightHaveTicket_BadRequest() throws Exception {
        CreateFlightCommand command = new CreateFlightCommand(
                "AA123", "Boston", "Houston",
                LocalDateTime.of(2025, 6, 4, 10, 0),
                LocalDateTime.of(2025, 6, 4, 14, 0),
                120
        );

        mockMvc.perform(put("/api/v1/flights/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Flight with id 1 has tickets. Cannot update flight with sold ticket."));
    }

    @Test
    void update_NotFound_ShouldReturnNotFound() throws Exception {
        CreateFlightCommand command = new CreateFlightCommand(
                "AA123", "Boston", "Houston",
                LocalDateTime.of(2025, 6, 4, 10, 0),
                LocalDateTime.of(2025, 6, 4, 14, 0),
                120
        );

        mockMvc.perform(put("/api/v1/flights/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isNotFound());
    }


    @Test
    void updateTime_ShouldUpdateFlightTime() throws Exception {
        LocalDateTime departureTime = LocalDateTime.of(2025, 6, 5, 11, 0);
        LocalDateTime arrivalTime = LocalDateTime.of(2025, 6, 5, 15, 0);
        UpdateFlightTimeCommand command = new UpdateFlightTimeCommand(
                departureTime,
                arrivalTime
        );

        mockMvc.perform(patch("/api/v1/flights/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.departureTime").value(departureTime.format(TIME_FORMAT)))
                .andExpect(jsonPath("$.arrivalTime").value(arrivalTime.format(TIME_FORMAT)));
    }

    @Test
    void delete_ShouldDeleteFlight() throws Exception {
        mockMvc.perform(delete("/api/v1/flights/1"))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/flights/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void delete_NotFound_ShouldReturnNotFound() throws Exception {
        mockMvc.perform(delete("/api/v1/flights/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllByFlightId_ShouldReturnTicketsForFlight() throws Exception {
        TicketSearchCriteria criteria = TicketSearchCriteria.builder().build();
        mockMvc.perform(get("/api/v1/flights/1/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(criteria))
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(2)));
    }

    @Test
    void createTicket_ShouldCreateTicketForFlight() throws Exception {
        CreateTicketCommand command = CreateTicketCommand.builder()
                .personId(4)
                .seatNumber(3)
                .price(BigDecimal.valueOf(100.0))
                .build();

        mockMvc.perform(post("/api/v1/flights/1/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.seatNumber").value(3))
                .andExpect(jsonPath("$.price").value(BigDecimal.valueOf(100.0)));
    }


    @Test
    void createTicket_seatNumberAlreadyTaken_badRequest() throws Exception {
        CreateTicketCommand command = CreateTicketCommand.builder()
                .personId(4)
                .seatNumber(1)
                .price(BigDecimal.valueOf(100.0))
                .build();

        mockMvc.perform(post("/api/v1/flights/1/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Flight with id 1 already has a ticket with seat number 1"));
    }

    @Test
    void createTicket_personHaveAlreadyTicket_badRequest() throws Exception {
        CreateTicketCommand command = CreateTicketCommand.builder()
                .personId(1)
                .seatNumber(4)
                .price(BigDecimal.valueOf(100.0))
                .build();

        mockMvc.perform(post("/api/v1/flights/1/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Person with id 1 already has a ticket for flight with id 1"));
    }

    @Test
    void updateTicketPerson_ShouldUpdatePersonForTicket() throws Exception {
        UpdateTicketPersonCommand command = UpdateTicketPersonCommand.builder()
                .personId(4)
                .build();

        mockMvc.perform(put("/api/v1/flights/1/tickets/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isOk());
    }
}