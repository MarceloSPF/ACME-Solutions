package com.acme.workshop.controller;

import com.acme.workshop.dto.ServiceOrderPartDTO;
import com.acme.workshop.model.ServiceOrderPart;
import com.acme.workshop.service.ServiceOrderPartService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/service-orders/{orderId}/parts")
public class ServiceOrderPartController {
    
    private final ServiceOrderPartService serviceOrderPartService;

    public ServiceOrderPartController(ServiceOrderPartService serviceOrderPartService) {
        this.serviceOrderPartService = serviceOrderPartService;
    }

    @PostMapping
    public ResponseEntity<ServiceOrderPartDTO> addPartToOrder(
            @PathVariable Long orderId,
            @RequestParam Long partId,
            @RequestParam Integer quantity) {
        ServiceOrderPart orderPart = serviceOrderPartService.addPartToOrder(orderId, partId, quantity);
        ServiceOrderPartDTO responseDTO = convertToDTO(orderPart);
        
        URI location = ServletUriComponentsBuilder
            .fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(orderPart.getId())
            .toUri();

        return ResponseEntity.created(location).body(responseDTO);
    }

    @GetMapping
    public ResponseEntity<List<ServiceOrderPartDTO>> getPartsByOrder(@PathVariable Long orderId) {
        List<ServiceOrderPartDTO> parts = serviceOrderPartService.findByServiceOrderId(orderId).stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(parts);
    }

    @PutMapping("/{orderPartId}/quantity")
    public ResponseEntity<ServiceOrderPartDTO> updateQuantity(
            @PathVariable Long orderId,
            @PathVariable Long orderPartId,
            @RequestParam Integer quantity) {
        ServiceOrderPart updatedPart = serviceOrderPartService.updateQuantity(orderPartId, quantity);
        return ResponseEntity.ok(convertToDTO(updatedPart));
    }

    @DeleteMapping("/{orderPartId}")
    public ResponseEntity<Void> removePartFromOrder(
            @PathVariable Long orderId,
            @PathVariable Long orderPartId) {
        serviceOrderPartService.removePartFromOrder(orderPartId);
        return ResponseEntity.noContent().build();
    }

    private ServiceOrderPartDTO convertToDTO(ServiceOrderPart orderPart) {
        ServiceOrderPartDTO dto = new ServiceOrderPartDTO();
        dto.setId(orderPart.getId());
        dto.setServiceOrderId(orderPart.getServiceOrder().getId());
        dto.setPartId(orderPart.getPart().getId());
        dto.setPartName(orderPart.getPart().getName());
        dto.setPartCode(orderPart.getPart().getCode());
        dto.setQuantity(orderPart.getQuantity());
        dto.setUnitPrice(orderPart.getUnitPrice());
        dto.setTotalCost(orderPart.getTotalCost());
        return dto;
    }
}

