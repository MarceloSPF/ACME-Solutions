package com.acme.workshop.controller;

import com.acme.workshop.model.Part;
import com.acme.workshop.repository.PartRepository;
import com.acme.workshop.util.TestReportExtension;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@TestReportExtension
public class PartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PartRepository partRepository;

    private Part testPart;

    @BeforeEach
    void setUp() {
        partRepository.deleteAll();
        testPart = new Part();
        testPart.setName("Filtro de óleo");
        testPart.setCode("FIL-001");
        testPart.setUnitPrice(new BigDecimal("25.50"));
        testPart.setStock(100);
        testPart = partRepository.save(testPart);
    }

    @Test
    void shouldCreatePart() throws Exception {
        Part newPart = new Part();
        newPart.setName("Pastilha de freio");
        newPart.setCode("PAS-001");
        newPart.setUnitPrice(new BigDecimal("50.00"));
        newPart.setStock(50);

        mockMvc.perform(post("/api/parts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newPart)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Pastilha de freio"))
                .andExpect(jsonPath("$.code").value("PAS-001"));
    }

    @Test
    void shouldGetAllParts() throws Exception {
        mockMvc.perform(get("/api/parts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name").value("Filtro de óleo"));
    }

    @Test
    void shouldGetPartById() throws Exception {
        mockMvc.perform(get("/api/parts/{id}", testPart.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testPart.getId()))
                .andExpect(jsonPath("$.name").value("Filtro de óleo"));
    }

    @Test
    void shouldGetPartByCode() throws Exception {
        mockMvc.perform(get("/api/parts/code/{code}", "FIL-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("FIL-001"));
    }

    @Test
    void shouldUpdatePart() throws Exception {
        testPart.setName("Filtro de óleo atualizado");

        mockMvc.perform(put("/api/parts/{id}", testPart.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testPart)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Filtro de óleo atualizado"));
    }

    @Test
    void shouldUpdateStock() throws Exception {
        mockMvc.perform(put("/api/parts/{id}/stock", testPart.getId())
                .param("quantity", "50"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stock").value(50));
    }

    @Test
    void shouldDeletePart() throws Exception {
        mockMvc.perform(delete("/api/parts/{id}", testPart.getId()))
                .andExpect(status().isNoContent());
    }
}

