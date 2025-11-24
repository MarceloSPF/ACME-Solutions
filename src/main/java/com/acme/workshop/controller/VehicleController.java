package com.acme.workshop.controller;

import com.acme.workshop.dto.VehicleDTO;
import com.acme.workshop.dto.VehicleSelectDTO;
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
    @PutMapping("/{id}")
    public ResponseEntity<VehicleDTO> updateVehicle(@PathVariable Long id, @RequestBody VehicleDTO vehicleDTO) {
        // Lembre-se de adicionar o método updateVehicle no WorkshopFacade também
        return ResponseEntity.ok(workshopFacade.updateVehicle(id, vehicleDTO));
    }
    @GetMapping("/select") // Endpoint: /api/customers/select
    public ResponseEntity<List<VehicleSelectDTO>> getVehiclesForDropdown() {
        return ResponseEntity.ok(workshopFacade.getVehiclesForSelect());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVehicle(@PathVariable Long id) {
        workshopFacade.deleteVehicle(id);
        return ResponseEntity.noContent().build();
    }
}