package com.acme.workshop.controller;

import com.acme.workshop.dto.*;
import com.acme.workshop.facade.WorkshopFacade;
import com.acme.workshop.model.ServiceOrder;
import com.acme.workshop.service.ServiceOrderService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/service-orders")
public class ServiceOrderController {
    
    private final WorkshopFacade workshopFacade;
    private final ServiceOrderService serviceOrderService;

    public ServiceOrderController(WorkshopFacade workshopFacade, ServiceOrderService serviceOrderService) {
        this.workshopFacade = workshopFacade;
        this.serviceOrderService = serviceOrderService;
    }

    @GetMapping
    public ResponseEntity<List<ServiceOrderResponseDTO>> getAllServiceOrders() {
        List<ServiceOrder> serviceOrders = serviceOrderService.findAll();
        List<ServiceOrderResponseDTO> dtos = serviceOrders.stream()
            .map(order -> {
                ServiceOrderResponseDTO dto = new ServiceOrderResponseDTO();
                dto.setId(order.getId());
                dto.setDescription(order.getDescription());
                dto.setStatus(order.getStatus());
                dto.setCreatedAt(order.getCreatedAt());
                dto.setCompletedAt(order.getCompletedAt());
                dto.setTotalCost(order.getTotalCost());

                // Customer DTO
                CustomerDTO customerDTO = new CustomerDTO();
                customerDTO.setId(order.getCustomer().getId());
                customerDTO.setName(order.getCustomer().getName());
                customerDTO.setEmail(order.getCustomer().getEmail());
                customerDTO.setPhone(order.getCustomer().getPhone());
                customerDTO.setAddress(order.getCustomer().getAddress());
                dto.setCustomer(customerDTO);

                // Vehicle DTO
                VehicleDTO vehicleDTO = new VehicleDTO();
                vehicleDTO.setId(order.getVehicle().getId());
                vehicleDTO.setBrand(order.getVehicle().getBrand());
                vehicleDTO.setModel(order.getVehicle().getModel());
                vehicleDTO.setModelYear(order.getVehicle().getModelYear());
                vehicleDTO.setLicensePlate(order.getVehicle().getLicensePlate());
                vehicleDTO.setCustomerId(order.getVehicle().getCustomer().getId());
                dto.setVehicle(vehicleDTO);

                // Technician DTO
                TechnicianDTO technicianDTO = new TechnicianDTO();
                technicianDTO.setId(order.getTechnician().getId());
                technicianDTO.setName(order.getTechnician().getName());
                technicianDTO.setEmail(order.getTechnician().getEmail());
                technicianDTO.setSpecialization(order.getTechnician().getSpecialization());
                dto.setTechnician(technicianDTO);

                return dto;
            })
            .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    @PostMapping
    public ResponseEntity<ServiceOrderResponseDTO> createServiceOrder(
            @Valid @RequestBody ServiceOrderRequestDTO requestDTO) {
        ServiceOrderResponseDTO responseDTO = workshopFacade.createServiceOrder(requestDTO);
        
        URI location = ServletUriComponentsBuilder
            .fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(responseDTO.getId())
            .toUri();

        return ResponseEntity.created(location).body(responseDTO);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ServiceOrderResponseDTO> updateServiceOrderStatus(
            @PathVariable Long id,
            @RequestBody ServiceOrder.ServiceStatus newStatus) {
        ServiceOrderResponseDTO updatedOrder = workshopFacade.updateServiceOrderStatus(id, newStatus);
        return ResponseEntity.ok(updatedOrder);
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<ServiceOrderResponseDTO>> getCustomerServiceOrders(
            @PathVariable Long customerId) {
        List<ServiceOrderResponseDTO> orders = workshopFacade.getCustomerServiceOrders(customerId);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/technician/{technicianId}")
    public ResponseEntity<List<ServiceOrderResponseDTO>> getTechnicianServiceOrders(
            @PathVariable Long technicianId) {
        List<ServiceOrderResponseDTO> orders = workshopFacade.getTechnicianServiceOrders(technicianId);
        return ResponseEntity.ok(orders);
    }
}