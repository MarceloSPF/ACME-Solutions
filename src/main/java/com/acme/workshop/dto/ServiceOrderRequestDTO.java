package com.acme.workshop.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero; // Importante
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import com.acme.workshop.model.ServiceOrder.ServiceStatus;

public class ServiceOrderRequestDTO {
    @NotNull(message = "ID do cliente é obrigatório")
    private Long customerId;
    @NotNull(message = "ID do veículo é obrigatório")
    private Long vehicleId;
    @NotNull(message = "ID do técnico é obrigatório")
    private Long technicianId;
    @NotBlank(message = "Descrição é obrigatória")
    private String description;
    
    @PositiveOrZero(message = "Custo total não pode ser negativo") // CORREÇÃO
    private BigDecimal totalCost;

    @Valid
    private List<ServiceItemDTO> serviceItems = new ArrayList<>();
    @Valid
    private List<ServiceOrderPartDTO> parts = new ArrayList<>();
    private ServiceStatus status;

    // Getters e Setters
    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
    public Long getVehicleId() { return vehicleId; }
    public void setVehicleId(Long vehicleId) { this.vehicleId = vehicleId; }
    public Long getTechnicianId() { return technicianId; }
    public void setTechnicianId(Long technicianId) { this.technicianId = technicianId; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public BigDecimal getTotalCost() { return totalCost; }
    public void setTotalCost(BigDecimal totalCost) { this.totalCost = totalCost; }
    public List<ServiceItemDTO> getServiceItems() { return serviceItems; }
    public void setServiceItems(List<ServiceItemDTO> serviceItems) { this.serviceItems = serviceItems; }
    public List<ServiceOrderPartDTO> getParts() { return parts; }
    public void setParts(List<ServiceOrderPartDTO> parts) { this.parts = parts; }
    public ServiceStatus getStatus() { return status; }
    public void setStatus(ServiceStatus status) { this.status = status; }
}