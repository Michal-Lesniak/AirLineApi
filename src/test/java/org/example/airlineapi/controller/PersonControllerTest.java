package org.example.airlineapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.airlineapi.model.person.PersonSearchCriteria;
import org.example.airlineapi.model.person.command.CreatePersonCommand;
import org.example.airlineapi.model.ticket.TicketSearchCriteria;
import org.example.airlineapi.repository.PersonRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
class PersonControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getAll_ShouldReturnListOfPersons() throws Exception {
        mockMvc.perform(get("/api/v1/persons"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(4)));
    }

    @Test
    void search_ShouldReturnPageOfPersons() throws Exception {
        PersonSearchCriteria criteria = PersonSearchCriteria.builder().firstName("Alice").build();
        mockMvc.perform(get("/api/v1/persons/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(criteria))
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(1)));
    }

    @Test
    void getPersonById_ShouldReturnPerson() throws Exception {
        mockMvc.perform(get("/api/v1/persons/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.firstName").value("Alice"));
    }

    @Test
    void getPersonById_NotFound_ShouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/persons/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void create_ShouldCreatePerson() throws Exception {
        CreatePersonCommand command = CreatePersonCommand.builder()
                .firstName("David")
                .lastName("Doe")
                .phoneNumber("098754321")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .email("david.doe@mail.com")
                .build();

        mockMvc.perform(post("/api/v1/persons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName").value("David"));
    }

    @Test
    void create_InvalidInput_ShouldReturnBadRequest() throws Exception {
        CreatePersonCommand command = CreatePersonCommand.builder()
                .firstName("")
                .lastName("")
                .phoneNumber("")
                .dateOfBirth(null)
                .email("")
                .build();

        mockMvc.perform(post("/api/v1/persons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void update_ShouldUpdatePerson() throws Exception {
        CreatePersonCommand command = CreatePersonCommand.builder()
                .firstName("Alice")
                .lastName("Doe")
                .phoneNumber("087654321")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .email("alice.newemail@example.com")
                .build();

        mockMvc.perform(put("/api/v1/persons/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("alice.newemail@example.com"))
                .andExpect(jsonPath("$.phoneNumber").value("087654321"));
    }

    @Test
    void update_NotFound_ShouldReturnNotFound() throws Exception {
        CreatePersonCommand command = CreatePersonCommand.builder()
                .firstName("Alice")
                .lastName("Doe")
                .phoneNumber("098765432")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .email("alice.newemail@example.com")
                .build();


        mockMvc.perform(put("/api/v1/persons/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isNotFound());
    }

    @Test
    void delete_ShouldDeletePerson() throws Exception {
        mockMvc.perform(delete("/api/v1/persons/4"))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/persons/4"))
                .andExpect(status().isNotFound());
    }

    @Test
    void delete_FlightHaveTickets_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(delete("/api/v1/persons/1"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(get("/api/v1/persons/1"))
                .andExpect(status().isOk());
    }

    @Test
    void delete_NotFound_ShouldReturnNotFound() throws Exception {
        mockMvc.perform(delete("/api/v1/persons/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllByPersonId_ShouldReturnTicketsForPerson() throws Exception {
        TicketSearchCriteria criteria = TicketSearchCriteria.builder().build();
        mockMvc.perform(get("/api/v1/persons/1/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(criteria))
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(2)));
    }
}
