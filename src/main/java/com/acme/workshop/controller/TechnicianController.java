package com.acme.workshop.controller;

import com.acme.workshop.dto.TechnicianDTO;
import com.acme.workshop.model.Technician;
import com.acme.workshop.service.TechnicianService;
import com.acme.workshop.facade.WorkshopFacade;
import com.acme.workshop.dto.TechnicianSelectDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/technicians")
public class TechnicianController {
    
    private final TechnicianService technicianService;
    private final WorkshopFacade workshopFacade;

    public TechnicianController(TechnicianService technicianService, WorkshopFacade workshopFacade) {
        this.technicianService = technicianService;
        this.workshopFacade = workshopFacade;
    }

    @PostMapping
    public ResponseEntity<TechnicianDTO> createTechnician(@Valid @RequestBody TechnicianDTO technicianDTO) {
        Technician technician = convertToEntity(technicianDTO);
        Technician savedTechnician = technicianService.save(technician);
        TechnicianDTO responseDTO = convertToDTO(savedTechnician);
        
        URI location = ServletUriComponentsBuilder
            .fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(savedTechnician.getId())
            .toUri();

        return ResponseEntity.created(location).body(responseDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TechnicianDTO> getTechnician(@PathVariable Long id) {
        return technicianService.findById(id)
            .map(this::convertToDTO)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<TechnicianDTO> getAllTechnicians() {
        return technicianService.findAll().stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    @PutMapping("/{id}")
    public ResponseEntity<TechnicianDTO> updateTechnician(
            @PathVariable Long id,
            @Valid @RequestBody TechnicianDTO technicianDTO) {
        Technician technician = convertToEntity(technicianDTO);
        Technician updatedTechnician = technicianService.update(id, technician);
        return ResponseEntity.ok(convertToDTO(updatedTechnician));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTechnician(@PathVariable Long id) {
        technicianService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search/specialization")
    public List<TechnicianDTO> searchBySpecialization(@RequestParam String specialization) {
        return technicianService.findBySpecialization(specialization).stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    private TechnicianDTO convertToDTO(Technician technician) {
        TechnicianDTO dto = new TechnicianDTO();
        dto.setId(technician.getId());
        dto.setName(technician.getName());
        dto.setEmail(technician.getEmail());
        dto.setSpecialization(technician.getSpecialization());
        return dto;
    }

    private Technician convertToEntity(TechnicianDTO dto) {
        Technician technician = new Technician();
        technician.setId(dto.getId());
        technician.setName(dto.getName());
        technician.setEmail(dto.getEmail());
        technician.setSpecialization(dto.getSpecialization());
        return technician;
    }
    @GetMapping("/select") // Endpoint: /api/customers/select
        public ResponseEntity<List<TechnicianSelectDTO>> getCustomersForDropdown() {
            return ResponseEntity.ok(workshopFacade.getTechniciansForSelect());
    }

}