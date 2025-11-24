package com.acme.workshop.repository;

import com.acme.workshop.model.ServiceOrderPart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceOrderPartRepository extends JpaRepository<ServiceOrderPart, Long> {
    
    List<ServiceOrderPart> findByServiceOrderId(Long serviceOrderId);
    
    List<ServiceOrderPart> findByPartId(Long partId);
}

