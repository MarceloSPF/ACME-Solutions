package com.acme.workshop.controller;

import com.acme.workshop.model.*;
import com.acme.workshop.repository.*;
import com.acme.workshop.util.TestReportExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
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
public class ServiceOrderPartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private TechnicianRepository technicianRepository;

    @Autowired
    private ServiceOrderRepository serviceOrderRepository;

    @Autowired
    private PartRepository partRepository;

    @Autowired
    private ServiceOrderPartRepository serviceOrderPartRepository;

    private ServiceOrder serviceOrder;
    private Part part;

    @BeforeEach
    void setUp() {
        serviceOrderPartRepository.deleteAll();
        serviceOrderRepository.deleteAll();
        vehicleRepository.deleteAll();
        customerRepository.deleteAll();
        technicianRepository.deleteAll();
        partRepository.deleteAll();

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

        // ✅ CORREÇÃO: Usar ServiceOrderBuilder
        serviceOrder = new ServiceOrderBuilder()
                .withCustomer(customer)
                .withVehicle(vehicle)
                .withTechnician(technician)
                .withDescription("Revisão")
                .withTotalCost(BigDecimal.ZERO)
                .build();
        serviceOrder = serviceOrderRepository.save(serviceOrder);

        part = new Part();
        part.setName("Filtro de óleo");
        part.setCode("FIL-001");
        part.setUnitPrice(new BigDecimal("25.50"));
        part.setStock(100);
        part = partRepository.save(part);
    }

    @Test
    void shouldAddPartToOrder() throws Exception {
        mockMvc.perform(post("/api/service-orders/{orderId}/parts", serviceOrder.getId())
                        .param("partId", part.getId().toString())
                        .param("quantity", "2"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.partId").value(part.getId()))
                .andExpect(jsonPath("$.quantity").value(2))
                .andExpect(jsonPath("$.totalCost").exists());
    }

    @Test
    void shouldGetPartsByOrder() throws Exception {
        // Adicionar peça primeiro
        ServiceOrderPart orderPart = new ServiceOrderPart();
        orderPart.setServiceOrder(serviceOrder);
        orderPart.setPart(part);
        orderPart.setQuantity(1);
        orderPart.setUnitPrice(part.getUnitPrice());
        serviceOrderPartRepository.save(orderPart);

        mockMvc.perform(get("/api/service-orders/{orderId}/parts", serviceOrder.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].partId").value(part.getId()));
    }

    @Test
    void shouldUpdateQuantity() throws Exception {
        // Adicionar peça primeiro
        ServiceOrderPart orderPart = new ServiceOrderPart();
        orderPart.setServiceOrder(serviceOrder);
        orderPart.setPart(part);
        orderPart.setQuantity(1);
        orderPart.setUnitPrice(part.getUnitPrice());
        orderPart = serviceOrderPartRepository.save(orderPart);

        mockMvc.perform(put("/api/service-orders/{orderId}/parts/{orderPartId}/quantity",
                        serviceOrder.getId(), orderPart.getId())
                        .param("quantity", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantity").value(3));
    }

    @Test
    void shouldRemovePartFromOrder() throws Exception {
        // Adicionar peça primeiro
        ServiceOrderPart orderPart = new ServiceOrderPart();
        orderPart.setServiceOrder(serviceOrder);
        orderPart.setPart(part);
        orderPart.setQuantity(1);
        orderPart.setUnitPrice(part.getUnitPrice());
        orderPart = serviceOrderPartRepository.save(orderPart);

        mockMvc.perform(delete("/api/service-orders/{orderId}/parts/{orderPartId}",
                        serviceOrder.getId(), orderPart.getId()))
                .andExpect(status().isNoContent());
    }
}