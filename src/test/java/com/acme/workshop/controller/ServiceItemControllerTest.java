package com.acme.workshop.controller;

import com.acme.workshop.model.*;
import com.acme.workshop.repository.*;
import com.acme.workshop.util.TestReportExtension;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
// ✅ SEM @Transactional
@TestReportExtension
public class ServiceItemControllerTest {

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

    @Autowired
    private ServiceItemRepository serviceItemRepository;

    private ServiceOrder serviceOrder;
    private ServiceItem serviceItem;

    @BeforeEach
    void setUp() {
        // Limpar em ordem correta
        serviceItemRepository.deleteAll();
        serviceOrderRepository.deleteAll();
        vehicleRepository.deleteAll();
        technicianRepository.deleteAll();
        customerRepository.deleteAll();

        // Criar dados de teste
        Customer customer = new Customer();
        customer.setName("Cliente Teste");
        customer.setEmail("cliente@test.com");
        customer.setPhone("(11) 99999-9999");
        customer.setAddress("Rua Teste");
        customer = customerRepository.save(customer);

        Vehicle vehicle = new Vehicle();
        vehicle.setBrand("Toyota");
        vehicle.setModel("Corolla");
        vehicle.setModelYear(2020);
        vehicle.setLicensePlate("ABC-1234");
        vehicle.setCustomer(customer);
        vehicle = vehicleRepository.save(vehicle);

        Technician technician = new Technician();
        technician.setName("Técnico Teste");
        technician.setEmail("tecnico@test.com");
        technician.setSpecialization("Motor");
        technician = technicianRepository.save(technician);

        serviceOrder = new ServiceOrderBuilder()
                .withCustomer(customer)
                .withVehicle(vehicle)
                .withTechnician(technician)
                .withDescription("Revisão")
                .withTotalCost(BigDecimal.ZERO)
                .build();
        serviceOrder = serviceOrderRepository.save(serviceOrder);

        serviceItem = new ServiceItem();
        serviceItem.setServiceOrder(serviceOrder);
        serviceItem.setDescription("Troca de óleo");
        serviceItem.setLaborCost(new BigDecimal("50.00"));
        serviceItem.setQuantity(1);
        serviceItem = serviceItemRepository.save(serviceItem);
    }

    @AfterEach
    void tearDown() {
        serviceItemRepository.deleteAll();
        serviceOrderRepository.deleteAll();
        vehicleRepository.deleteAll();
        technicianRepository.deleteAll();
        customerRepository.deleteAll();
    }

    @Test
    void shouldCreateServiceItem() throws Exception {
        Map<String, Object> request = new HashMap<>();
        request.put("serviceOrderId", serviceOrder.getId());
        request.put("description", "Alinhamento");
        request.put("laborCost", 80.00);
        request.put("quantity", 1);

        mockMvc.perform(post("/api/service-items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.description").value("Alinhamento"))
                .andExpect(jsonPath("$.totalCost").exists());
    }

    @Test
    void shouldGetServiceItemById() throws Exception {
        mockMvc.perform(get("/api/service-items/{id}", serviceItem.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(serviceItem.getId()))
                .andExpect(jsonPath("$.description").value("Troca de óleo"));
    }

    @Test
    void shouldGetServiceItemsByOrder() throws Exception {
        mockMvc.perform(get("/api/service-items/order/{orderId}", serviceOrder.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].description").value("Troca de óleo"));
    }

    @Test
    void shouldUpdateServiceItem() throws Exception {
        // Verificar que o item existe
        Long itemId = serviceItem.getId();

        Map<String, Object> request = new HashMap<>();
        request.put("serviceOrderId", serviceOrder.getId());
        request.put("description", "Troca de óleo atualizada");
        request.put("laborCost", 60.00);
        request.put("quantity", 2);

        mockMvc.perform(put("/api/service-items/{id}", itemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("Troca de óleo atualizada"));
    }

    @Test
    void shouldDeleteServiceItem() throws Exception {
        mockMvc.perform(delete("/api/service-items/{id}", serviceItem.getId()))
                .andExpect(status().isNoContent());
    }
}