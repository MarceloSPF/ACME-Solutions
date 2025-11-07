package com.acme.workshop.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public class ServiceOrderRequestDTO {
    @NotNull(message = "ID do cliente é obrigatório")
    private Long customerId;
    
    @NotNull(message = "ID do veículo é obrigatório")
    private Long vehicleId;
    
    @NotNull(message = "ID do técnico é obrigatório")
    private Long technicianId;
    
    @NotBlank(message = "Descrição é obrigatória")
    private String description;
    
    @NotNull(message = "Custo total é obrigatório")
    @Positive(message = "Custo total deve ser maior que zero")
    private BigDecimal totalCost;

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
}