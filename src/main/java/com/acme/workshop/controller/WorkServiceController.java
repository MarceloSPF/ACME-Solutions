package com.acme.workshop.controller;

import com.acme.workshop.dto.WorkServiceDTO;
import com.acme.workshop.facade.WorkshopFacade;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/work-services")
public class WorkServiceController {

    private final WorkshopFacade workshopFacade;

    public WorkServiceController(WorkshopFacade workshopFacade) {
        this.workshopFacade = workshopFacade;
    }

    @GetMapping
    public ResponseEntity<List<WorkServiceDTO>> getAllWorkServices() {
        return ResponseEntity.ok(workshopFacade.getAllWorkServices());
    }

    @PostMapping
    public ResponseEntity<WorkServiceDTO> createWorkService(@RequestBody WorkServiceDTO workServiceDTO) {
        WorkServiceDTO savedService = workshopFacade.createWorkService(workServiceDTO);
        
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedService.getId())
                .toUri();
                
        return ResponseEntity.created(location).body(savedService);
    }

    @PutMapping("/{id}")
    public ResponseEntity<WorkServiceDTO> updateWorkService(@PathVariable Long id, @RequestBody WorkServiceDTO workServiceDTO) {
        return ResponseEntity.ok(workshopFacade.updateWorkService(id, workServiceDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWorkService(@PathVariable Long id) {
        workshopFacade.deleteWorkService(id);
        return ResponseEntity.noContent().build();
    }
}