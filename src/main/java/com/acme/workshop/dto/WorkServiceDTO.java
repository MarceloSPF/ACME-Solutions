package com.acme.workshop.dto;
import java.math.BigDecimal;

public class WorkServiceDTO {
    private Long id;
    private String description;
    private BigDecimal standardPrice;

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public BigDecimal getStandardPrice() { return standardPrice; }
    public void setStandardPrice(BigDecimal standardPrice) { this.standardPrice = standardPrice; }
}