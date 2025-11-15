package com.acme.workshop.service;

import com.acme.workshop.model.*;
import com.acme.workshop.repository.ServiceOrderRepository;
import com.acme.workshop.service.observer.ServiceOrderObserver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ServiceOrderService {
    
    private final ServiceOrderRepository serviceOrderRepository;
    private final List<ServiceOrderObserver> observers = new ArrayList<>();

    @Autowired
    public ServiceOrderService(ServiceOrderRepository serviceOrderRepository) {
        this.serviceOrderRepository = serviceOrderRepository;
    }

    public void addObserver(ServiceOrderObserver observer) {
        observers.add(observer);
    }

    public ServiceOrder save(ServiceOrder serviceOrder) {
        return serviceOrderRepository.save(serviceOrder);
    }

    public void removeObserver(ServiceOrderObserver observer) {
        observers.remove(observer);
    }

    private void notifyObservers(ServiceOrder serviceOrder, ServiceOrder.ServiceStatus oldStatus) {
        observers.forEach(observer -> observer.onServiceOrderStatusChange(serviceOrder, oldStatus));
    }

    public ServiceOrder createServiceOrder(Customer customer, Vehicle vehicle, 
                                        Technician technician, String description) {
        ServiceOrder serviceOrder = new ServiceOrderBuilder()
            .withCustomer(customer)
            .withVehicle(vehicle)
            .withTechnician(technician)
            .withDescription(description)
            .withTotalCost(java.math.BigDecimal.ZERO) // Será atualizado posteriormente
            .build();

        return serviceOrderRepository.save(serviceOrder);
    }

    public ServiceOrder updateStatus(Long id, ServiceOrder.ServiceStatus newStatus) {
        ServiceOrder serviceOrder = serviceOrderRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Ordem de serviço não encontrada"));

        ServiceOrder.ServiceStatus oldStatus = serviceOrder.getStatus();
        serviceOrder.setStatus(newStatus);

        if (newStatus == ServiceOrder.ServiceStatus.COMPLETED) {
            serviceOrder.setCompletedAt(LocalDateTime.now());
        }

        serviceOrder = serviceOrderRepository.save(serviceOrder);
        
        // Notifica os observers sobre a mudança de status
        notifyObservers(serviceOrder, oldStatus);
        
        return serviceOrder;
    }

    public Optional<ServiceOrder> findById(Long id) {
        return serviceOrderRepository.findById(id);
    }

    public List<ServiceOrder> findAll() {
        return serviceOrderRepository.findAll();
    }

    public List<ServiceOrder> findByCustomerId(Long customerId) {
        return serviceOrderRepository.findByCustomerId(customerId);
    }

    public List<ServiceOrder> findByStatus(ServiceOrder.ServiceStatus status) {
        return serviceOrderRepository.findByStatus(status);
    }

    public List<ServiceOrder> findByTechnicianId(Long technicianId) {
        return serviceOrderRepository.findByTechnicianId(technicianId);
    }

    public ServiceOrder updateTotalCost(Long id, java.math.BigDecimal totalCost) {
        ServiceOrder serviceOrder = serviceOrderRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Ordem de serviço não encontrada"));

        serviceOrder.setTotalCost(totalCost);
        return serviceOrderRepository.save(serviceOrder);
    }

    // Recalcula o custo total automaticamente baseado em itens e peças
    public ServiceOrder recalculateTotalCost(Long id) {
        ServiceOrder serviceOrder = serviceOrderRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Ordem de serviço não encontrada"));

        serviceOrder.updateTotalCost();
        return serviceOrderRepository.save(serviceOrder);
    }
}