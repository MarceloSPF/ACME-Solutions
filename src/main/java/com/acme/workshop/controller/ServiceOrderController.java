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
    public ResponseEntity<List<ServiceOrderSummaryDTO>> getAllServiceOrders() {
        return ResponseEntity.ok(workshopFacade.getServiceOrderSummaries());
    }

    // --- O MÉTODO QUE FALTAVA (POST) ---
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
    // -----------------------------------

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

    @PutMapping("/{id}")
    public ResponseEntity<ServiceOrderResponseDTO> updateServiceOrder(
            @PathVariable Long id, 
            @Valid @RequestBody ServiceOrderRequestDTO requestDTO) {
        return ResponseEntity.ok(workshopFacade.updateServiceOrder(id, requestDTO));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServiceOrderResponseDTO> getServiceOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(workshopFacade.getServiceOrderById(id));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteServiceOrder(@PathVariable Long id) {
        serviceOrderService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}