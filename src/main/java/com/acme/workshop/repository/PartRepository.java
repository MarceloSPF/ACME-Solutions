package com.acme.workshop.repository;

import com.acme.workshop.model.Part;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PartRepository extends JpaRepository<Part, Long> {
    
    Optional<Part> findByCode(String code);
    
    boolean existsByCode(String code);
}

