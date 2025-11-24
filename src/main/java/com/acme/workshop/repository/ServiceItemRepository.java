package com.acme.workshop.repository;

import com.acme.workshop.model.ServiceItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceItemRepository extends JpaRepository<ServiceItem, Long> {
    
    List<ServiceItem> findByServiceOrderId(Long serviceOrderId);
}

