package com.acme.workshop.repository;

import com.acme.workshop.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    
    Optional<Vehicle> findByLicensePlate(String licensePlate);
    
    List<Vehicle> findByCustomerId(Long customerId);
    
    List<Vehicle> findByBrandAndModel(String brand, String model);
    
    List<Vehicle> findByModelYear(Integer modelYear);
    
    boolean existsByLicensePlate(String licensePlate);
}