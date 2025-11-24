package com.acme.workshop.dto;

import com.acme.workshop.model.ServiceOrder.ServiceStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ServiceOrderSummaryDTO {
    private Long id;
    private String customerName;
    private String vehicleInfo; // Ex: "Toyota Corolla (ABC-1234)"
    private String technicianName;
    private ServiceStatus status;
    private LocalDateTime createdAt;
    private BigDecimal totalCost;

    // Construtor
    public ServiceOrderSummaryDTO(Long id, String customerName, String vehicleInfo, 
                                String technicianName, ServiceStatus status, 
                                LocalDateTime createdAt, BigDecimal totalCost) {
        this.id = id;
        this.customerName = customerName;
        this.vehicleInfo = vehicleInfo;
        this.technicianName = technicianName;
        this.status = status;
        this.createdAt = createdAt;
        this.totalCost = totalCost;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public String getVehicleInfo() { return vehicleInfo; }
    public void setVehicleInfo(String vehicleInfo) { this.vehicleInfo = vehicleInfo; }
    public String getTechnicianName() { return technicianName; }
    public void setTechnicianName(String technicianName) { this.technicianName = technicianName; }
    public ServiceStatus getStatus() { return status; }
    public void setStatus(ServiceStatus status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public BigDecimal getTotalCost() { return totalCost; }
    public void setTotalCost(BigDecimal totalCost) { this.totalCost = totalCost; }
}