package com.acme.workshop.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "service_items")
public class ServiceItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "service_order_id", nullable = false)
    private ServiceOrder serviceOrder;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private BigDecimal laborCost;

    @Column(nullable = false)
    private Integer quantity;

    // Calcula o custo total do item (laborCost * quantity)
    public BigDecimal getTotalCost() {
        if (laborCost == null || quantity == null) {
            return BigDecimal.ZERO;
        }
        return laborCost.multiply(BigDecimal.valueOf(quantity));
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ServiceOrder getServiceOrder() {
        return serviceOrder;
    }

    public void setServiceOrder(ServiceOrder serviceOrder) {
        this.serviceOrder = serviceOrder;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getLaborCost() {
        return laborCost;
    }

    public void setLaborCost(BigDecimal laborCost) {
        this.laborCost = laborCost;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}

