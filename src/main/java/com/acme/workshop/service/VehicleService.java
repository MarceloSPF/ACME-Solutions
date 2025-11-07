package com.acme.workshop.service;

import com.acme.workshop.model.Vehicle;
import com.acme.workshop.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class VehicleService {
    
    private final VehicleRepository vehicleRepository;

    @Autowired
    public VehicleService(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    public Vehicle save(Vehicle vehicle) {
        if (vehicleRepository.existsByLicensePlate(vehicle.getLicensePlate())) {
            throw new IllegalArgumentException("Placa já cadastrada");
        }
        return vehicleRepository.save(vehicle);
    }

    public Optional<Vehicle> findById(Long id) {
        return vehicleRepository.findById(id);
    }

    public List<Vehicle> findAll() {
        return vehicleRepository.findAll();
    }

    public List<Vehicle> findByCustomerId(Long customerId) {
        return vehicleRepository.findByCustomerId(customerId);
    }

    public void delete(Long id) {
        vehicleRepository.deleteById(id);
    }

    public Vehicle update(Long id, Vehicle vehicleDetails) {
        Vehicle vehicle = vehicleRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Veículo não encontrado"));

        // Verifica se a nova placa já existe (se foi alterada)
        if (!vehicle.getLicensePlate().equals(vehicleDetails.getLicensePlate()) &&
            vehicleRepository.existsByLicensePlate(vehicleDetails.getLicensePlate())) {
            throw new IllegalArgumentException("Placa já cadastrada");
        }

        vehicle.setBrand(vehicleDetails.getBrand());
        vehicle.setModel(vehicleDetails.getModel());
        vehicle.setModelYear(vehicleDetails.getModelYear());
        vehicle.setLicensePlate(vehicleDetails.getLicensePlate());
        vehicle.setCustomer(vehicleDetails.getCustomer());

        return vehicleRepository.save(vehicle);
    }
}