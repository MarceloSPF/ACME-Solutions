package com.acme.workshop.repository;
import com.acme.workshop.model.WorkService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkServiceRepository extends JpaRepository<WorkService, Long> { }