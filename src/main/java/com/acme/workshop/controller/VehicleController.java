package com.acme.workshop.controller;

import com.acme.workshop.dto.VehicleDTO;
import com.acme.workshop.facade.WorkshopFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vehicles")
public class VehicleController {

    private final WorkshopFacade workshopFacade;

    @Autowired
    public VehicleController(WorkshopFacade workshopFacade) {
        this.workshopFacade = workshopFacade;
    }

    @PostMapping
    public ResponseEntity<VehicleDTO> createVehicle(@RequestBody VehicleDTO vehicleDTO) {
        return ResponseEntity.ok(workshopFacade.createVehicle(vehicleDTO));
    }

    @GetMapping
    public ResponseEntity<List<VehicleDTO>> getAllVehicles() {
        return ResponseEntity.ok(workshopFacade.getAllVehicles());
    }

    @GetMapping("/{id}")
    public ResponseEntity<VehicleDTO> getVehicle(@PathVariable Long id) {
        return ResponseEntity.ok(workshopFacade.getVehicle(id));
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<VehicleDTO>> getVehiclesByCustomer(@PathVariable Long customerId) {
        return ResponseEntity.ok(workshopFacade.getVehiclesByCustomer(customerId));
    }
}