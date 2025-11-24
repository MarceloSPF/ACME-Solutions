package com.acme.workshop.controller;

import com.acme.workshop.model.*;
import com.acme.workshop.repository.*;
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
import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@TestReportExtension
public class ServiceOrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private TechnicianRepository technicianRepository;

    @Autowired
    private ServiceOrderRepository serviceOrderRepository;

    private Customer customer;
    private Vehicle vehicle;
    private Technician technician;
    private ServiceOrder serviceOrder;

    @BeforeEach
    void setUp() {
        // Limpar dados
        serviceOrderRepository.deleteAll();
        vehicleRepository.deleteAll();
        customerRepository.deleteAll();
        technicianRepository.deleteAll();

        // Criar cliente
        customer = new Customer();
        customer.setName("Cliente Teste");
        customer.setEmail("cliente@test.com");
        customer.setPhone("(11) 99999-9999");
        customer.setAddress("Rua Teste, 123");
        customer = customerRepository.save(customer);

        // Criar veículo
        vehicle = new Vehicle();
        vehicle.setBrand("Toyota");
        vehicle.setModel("Corolla");
        vehicle.setModelYear(2020);
        vehicle.setLicensePlate("ABC-1234");
        vehicle.setCustomer(customer);
        vehicle = vehicleRepository.save(vehicle);

        // Criar técnico
        technician = new Technician();
        technician.setName("Técnico Teste");
        technician.setEmail("tecnico@test.com");
        technician.setSpecialization("Motor");
        technician = technicianRepository.save(technician);

        // ✅ CORREÇÃO: Usar ServiceOrderBuilder ao invés de criar manualmente
        serviceOrder = new ServiceOrderBuilder()
                .withCustomer(customer)
                .withVehicle(vehicle)
                .withTechnician(technician)
                .withDescription("Revisão completa")
                .withTotalCost(BigDecimal.ZERO)
                .build();
        serviceOrder = serviceOrderRepository.save(serviceOrder);
    }

    @Test
    void shouldCreateServiceOrder() throws Exception {
        Map<String, Object> request = new HashMap<>();
        request.put("customerId", customer.getId());
        request.put("vehicleId", vehicle.getId());
        request.put("technicianId", technician.getId());
        request.put("description", "Nova ordem de serviço");
        request.put("totalCost", 150.00);  // ✅ Adicionar totalCost

        mockMvc.perform(post("/api/service-orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.description").value("Nova ordem de serviço"))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void shouldGetAllServiceOrders() throws Exception {
        mockMvc.perform(get("/api/service-orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].description").value("Revisão completa"));
    }

    @Test
    void shouldUpdateServiceOrderStatus() throws Exception {
        mockMvc.perform(put("/api/service-orders/{id}/status", serviceOrder.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("\"IN_PROGRESS\""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));
    }

    @Test
    void shouldGetServiceOrdersByCustomer() throws Exception {
        mockMvc.perform(get("/api/service-orders/customer/{customerId}", customer.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].customer.id").value(customer.getId()));
    }

    @Test
    void shouldGetServiceOrdersByTechnician() throws Exception {
        mockMvc.perform(get("/api/service-orders/technician/{technicianId}", technician.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].technician.id").value(technician.getId()));
    }
}