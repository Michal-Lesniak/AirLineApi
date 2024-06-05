package org.example.airlineapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.airlineapi.repository.FlightRepository;
import org.example.airlineapi.service.FlightService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = "/schema.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Sql(scripts = "/init_data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/remove_data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class FlightControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FlightRepository flightRepository;

    @Autowired
    private ObjectMapper objectMapper;



}