package com.acme.workshop.repository;

import com.acme.workshop.model.Technician;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface TechnicianRepository extends JpaRepository<Technician, Long> {
    
    Optional<Technician> findByEmail(String email);
    
    List<Technician> findBySpecialization(String specialization);
    
    List<Technician> findByNameContainingIgnoreCase(String name);
    
    boolean existsByEmail(String email);
}