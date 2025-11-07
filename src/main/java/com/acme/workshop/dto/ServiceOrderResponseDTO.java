package com.acme.workshop.dto;

import com.acme.workshop.model.ServiceOrder.ServiceStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ServiceOrderResponseDTO {
    private Long id;
    private CustomerDTO customer;
    private VehicleDTO vehicle;
    private TechnicianDTO technician;
    private String description;
    private ServiceStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
    private BigDecimal totalCost;

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public CustomerDTO getCustomer() { return customer; }
    public void setCustomer(CustomerDTO customer) { this.customer = customer; }
    public VehicleDTO getVehicle() { return vehicle; }
    public void setVehicle(VehicleDTO vehicle) { this.vehicle = vehicle; }
    public TechnicianDTO getTechnician() { return technician; }
    public void setTechnician(TechnicianDTO technician) { this.technician = technician; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public ServiceStatus getStatus() { return status; }
    public void setStatus(ServiceStatus status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
    public BigDecimal getTotalCost() { return totalCost; }
    public void setTotalCost(BigDecimal totalCost) { this.totalCost = totalCost; }
}