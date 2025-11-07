package com.acme.workshop.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ServiceOrderBuilder {
    private final ServiceOrder serviceOrder;

    public ServiceOrderBuilder() {
        serviceOrder = new ServiceOrder();
        serviceOrder.setCreatedAt(LocalDateTime.now());
        serviceOrder.setStatus(ServiceOrder.ServiceStatus.PENDING);
    }

    public ServiceOrderBuilder withCustomer(Customer customer) {
        serviceOrder.setCustomer(customer);
        return this;
    }

    public ServiceOrderBuilder withVehicle(Vehicle vehicle) {
        serviceOrder.setVehicle(vehicle);
        return this;
    }

    public ServiceOrderBuilder withTechnician(Technician technician) {
        serviceOrder.setTechnician(technician);
        return this;
    }

    public ServiceOrderBuilder withDescription(String description) {
        serviceOrder.setDescription(description);
        return this;
    }

    public ServiceOrderBuilder withTotalCost(BigDecimal totalCost) {
        serviceOrder.setTotalCost(totalCost);
        return this;
    }

    public ServiceOrderBuilder withStatus(ServiceOrder.ServiceStatus status) {
        serviceOrder.setStatus(status);
        return this;
    }

    public ServiceOrder build() {
        validateServiceOrder();
        return serviceOrder;
    }

    private void validateServiceOrder() {
        if (serviceOrder.getCustomer() == null) {
            throw new IllegalStateException("Customer is required");
        }
        if (serviceOrder.getVehicle() == null) {
            throw new IllegalStateException("Vehicle is required");
        }
        if (serviceOrder.getTechnician() == null) {
            throw new IllegalStateException("Technician is required");
        }
        if (serviceOrder.getDescription() == null || serviceOrder.getDescription().trim().isEmpty()) {
            throw new IllegalStateException("Description is required");
        }
        if (serviceOrder.getTotalCost() == null) {
            throw new IllegalStateException("Total cost is required");
        }
    }
}