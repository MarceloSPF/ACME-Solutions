package com.acme.workshop.repository;

import com.acme.workshop.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    
    Optional<Customer> findByEmail(String email);
    
    List<Customer> findByNameContainingIgnoreCase(String name);
    
    boolean existsByEmail(String email);
    
    List<Customer> findByPhoneContaining(String phone);
}