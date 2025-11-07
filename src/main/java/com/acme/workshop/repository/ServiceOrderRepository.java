package com.acme.workshop.repository;

import com.acme.workshop.model.ServiceOrder;
import com.acme.workshop.model.ServiceOrder.ServiceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ServiceOrderRepository extends JpaRepository<ServiceOrder, Long> {
    
    List<ServiceOrder> findByCustomerId(Long customerId);
    
    List<ServiceOrder> findByVehicleId(Long vehicleId);
    
    List<ServiceOrder> findByTechnicianId(Long technicianId);
    
    List<ServiceOrder> findByStatus(ServiceStatus status);
    
    List<ServiceOrder> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    List<ServiceOrder> findByCustomerIdAndStatus(Long customerId, ServiceStatus status);
    
    List<ServiceOrder> findByTechnicianIdAndStatus(Long technicianId, ServiceStatus status);
}