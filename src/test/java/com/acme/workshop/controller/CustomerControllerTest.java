package com.acme.workshop.controller;

import com.acme.workshop.dto.CustomerDTO;
import com.acme.workshop.model.Customer;
import com.acme.workshop.repository.CustomerRepository;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@TestReportExtension
public class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CustomerRepository customerRepository;

    private Customer testCustomer;

    @BeforeEach
    void setUp() {
        customerRepository.deleteAll();
        testCustomer = new Customer();
        testCustomer.setName("João Silva");
        testCustomer.setEmail("joao@test.com");
        testCustomer.setPhone("11999999999");
        testCustomer.setAddress("Rua Teste, 123");
        testCustomer = customerRepository.save(testCustomer);
    }

    @Test
    void shouldCreateCustomer() throws Exception {
        CustomerDTO newCustomer = new CustomerDTO();
        newCustomer.setName("Maria Santos");
        newCustomer.setEmail("maria@test.com");
        newCustomer.setPhone("11999999999");
        newCustomer.setAddress("Rua Nova, 456");

        mockMvc.perform(post("/api/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newCustomer)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Maria Santos"))
                .andExpect(jsonPath("$.email").value("maria@test.com"));
    }

    @Test
    void shouldGetAllCustomers() throws Exception {
        mockMvc.perform(get("/api/customers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name").value("João Silva"));
    }

    @Test
    void shouldGetCustomerById() throws Exception {
        mockMvc.perform(get("/api/customers/{id}", testCustomer.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testCustomer.getId()))
                .andExpect(jsonPath("$.name").value("João Silva"));
    }

    @Test
    void shouldReturnNotFoundForInvalidId() throws Exception {
        mockMvc.perform(get("/api/customers/99999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldUpdateCustomer() throws Exception {
        CustomerDTO customerDTO = new CustomerDTO();
        customerDTO.setId(testCustomer.getId());
        customerDTO.setName("João Silva Atualizado");
        customerDTO.setEmail(testCustomer.getEmail());
        customerDTO.setPhone(testCustomer.getPhone());
        customerDTO.setAddress(testCustomer.getAddress());

        mockMvc.perform(put("/api/customers/{id}", testCustomer.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(customerDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("João Silva Atualizado"));
    }

    @Test
    void shouldDeleteCustomer() throws Exception {
        mockMvc.perform(delete("/api/customers/{id}", testCustomer.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldSearchCustomersByName() throws Exception {
        mockMvc.perform(get("/api/customers/search")
                .param("name", "João"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name").value("João Silva"));
    }
}

