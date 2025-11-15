package com.acme.workshop.controller;

import com.acme.workshop.dto.ServiceItemDTO;
import com.acme.workshop.model.ServiceItem;
import com.acme.workshop.model.ServiceOrder;
import com.acme.workshop.service.ServiceItemService;
import com.acme.workshop.service.ServiceOrderService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/service-items")
public class ServiceItemController {
    
    private final ServiceItemService serviceItemService;
    private final ServiceOrderService serviceOrderService;

    public ServiceItemController(ServiceItemService serviceItemService, 
                                ServiceOrderService serviceOrderService) {
        this.serviceItemService = serviceItemService;
        this.serviceOrderService = serviceOrderService;
    }

    @PostMapping
    public ResponseEntity<ServiceItemDTO> createServiceItem(@Valid @RequestBody ServiceItemDTO serviceItemDTO) {
        ServiceItem serviceItem = convertToEntity(serviceItemDTO);
        ServiceItem savedItem = serviceItemService.addServiceItem(
            serviceItemDTO.getServiceOrderId(), 
            serviceItem
        );
        ServiceItemDTO responseDTO = convertToDTO(savedItem);
        
        URI location = ServletUriComponentsBuilder
            .fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(savedItem.getId())
            .toUri();

        return ResponseEntity.created(location).body(responseDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServiceItemDTO> getServiceItem(@PathVariable Long id) {
        try {
            ServiceItem item = serviceItemService.findById(id);
            return ResponseEntity.ok(convertToDTO(item));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<ServiceItemDTO>> getServiceItemsByOrder(@PathVariable Long orderId) {
        List<ServiceItemDTO> items = serviceItemService.findByServiceOrderId(orderId).stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(items);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ServiceItemDTO> updateServiceItem(
            @PathVariable Long id,
            @Valid @RequestBody ServiceItemDTO serviceItemDTO) {
        // Buscar o item existente
        ServiceOrder order = serviceOrderService.findById(serviceItemDTO.getServiceOrderId())
            .orElseThrow(() -> new IllegalArgumentException("Ordem de serviço não encontrada"));
        
        ServiceItem existingItem = order.getServiceItems().stream()
            .filter(item -> item.getId().equals(id))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Item de serviço não encontrado"));
        
        // Atualizar os campos
        existingItem.setDescription(serviceItemDTO.getDescription());
        existingItem.setLaborCost(serviceItemDTO.getLaborCost());
        existingItem.setQuantity(serviceItemDTO.getQuantity());
        
        ServiceItem updatedItem = serviceItemService.save(existingItem);
        return ResponseEntity.ok(convertToDTO(updatedItem));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteServiceItem(@PathVariable Long id) {

        serviceItemService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private ServiceItemDTO convertToDTO(ServiceItem serviceItem) {
        ServiceItemDTO dto = new ServiceItemDTO();
        dto.setId(serviceItem.getId());
        dto.setServiceOrderId(serviceItem.getServiceOrder().getId());
        dto.setDescription(serviceItem.getDescription());
        dto.setLaborCost(serviceItem.getLaborCost());
        dto.setQuantity(serviceItem.getQuantity());
        dto.setTotalCost(serviceItem.getTotalCost());
        return dto;
    }

    private ServiceItem convertToEntity(ServiceItemDTO dto) {
        ServiceItem serviceItem = new ServiceItem();
        serviceItem.setDescription(dto.getDescription());
        serviceItem.setLaborCost(dto.getLaborCost());
        serviceItem.setQuantity(dto.getQuantity() != null ? dto.getQuantity() : 1);
        return serviceItem;
    }
}

