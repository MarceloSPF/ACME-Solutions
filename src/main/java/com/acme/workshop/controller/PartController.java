package com.acme.workshop.controller;

import com.acme.workshop.dto.PartDTO;
import com.acme.workshop.model.Part;
import com.acme.workshop.service.PartService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/parts")
public class PartController {
    
    private final PartService partService;

    public PartController(PartService partService) {
        this.partService = partService;
    }

    @PostMapping
    public ResponseEntity<PartDTO> createPart(@Valid @RequestBody PartDTO partDTO) {
        Part part = convertToEntity(partDTO);
        Part savedPart = partService.save(part);
        PartDTO responseDTO = convertToDTO(savedPart);
        
        URI location = ServletUriComponentsBuilder
            .fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(savedPart.getId())
            .toUri();

        return ResponseEntity.created(location).body(responseDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PartDTO> getPart(@PathVariable Long id) {
        return partService.findById(id)
            .map(this::convertToDTO)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<PartDTO>> getAllParts() {
        List<PartDTO> parts = partService.findAll().stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(parts);
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<PartDTO> getPartByCode(@PathVariable String code) {
        return partService.findByCode(code)
            .map(this::convertToDTO)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<PartDTO> updatePart(
            @PathVariable Long id,
            @Valid @RequestBody PartDTO partDTO) {
        Part part = convertToEntity(partDTO);
        part.setId(id);
        Part updatedPart = partService.save(part);
        return ResponseEntity.ok(convertToDTO(updatedPart));
    }

    @PutMapping("/{id}/stock")
    public ResponseEntity<PartDTO> updateStock(
            @PathVariable Long id,
            @RequestParam Integer quantity) {
        Part updatedPart = partService.updateStock(id, quantity);
        return ResponseEntity.ok(convertToDTO(updatedPart));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePart(@PathVariable Long id) {
        partService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private PartDTO convertToDTO(Part part) {
        PartDTO dto = new PartDTO();
        dto.setId(part.getId());
        dto.setName(part.getName());
        dto.setCode(part.getCode());
        dto.setUnitPrice(part.getUnitPrice());
        dto.setStock(part.getStock());
        return dto;
    }

    private Part convertToEntity(PartDTO dto) {
        Part part = new Part();
        part.setId(dto.getId());
        part.setName(dto.getName());
        part.setCode(dto.getCode());
        part.setUnitPrice(dto.getUnitPrice());
        part.setStock(dto.getStock() != null ? dto.getStock() : 0);
        return part;
    }
}

