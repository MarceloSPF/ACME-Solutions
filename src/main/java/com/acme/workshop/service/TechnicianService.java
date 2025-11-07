package com.acme.workshop.service;

import com.acme.workshop.model.Technician;
import com.acme.workshop.repository.TechnicianRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TechnicianService {
    
    private final TechnicianRepository technicianRepository;

    @Autowired
    public TechnicianService(TechnicianRepository technicianRepository) {
        this.technicianRepository = technicianRepository;
    }

    public Technician save(Technician technician) {
        if (technicianRepository.existsByEmail(technician.getEmail())) {
            throw new IllegalArgumentException("Email já cadastrado");
        }
        return technicianRepository.save(technician);
    }

    public Optional<Technician> findById(Long id) {
        return technicianRepository.findById(id);
    }

    public List<Technician> findAll() {
        return technicianRepository.findAll();
    }

    public List<Technician> findBySpecialization(String specialization) {
        return technicianRepository.findBySpecialization(specialization);
    }

    public Optional<Technician> findByEmail(String email) {
        return technicianRepository.findByEmail(email);
    }

    public void delete(Long id) {
        technicianRepository.deleteById(id);
    }

    public Technician update(Long id, Technician technicianDetails) {
        Technician technician = technicianRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Técnico não encontrado"));

        if (!technician.getEmail().equals(technicianDetails.getEmail()) &&
            technicianRepository.existsByEmail(technicianDetails.getEmail())) {
            throw new IllegalArgumentException("Email já cadastrado");
        }

        technician.setName(technicianDetails.getName());
        technician.setEmail(technicianDetails.getEmail());
        technician.setSpecialization(technicianDetails.getSpecialization());

        return technicianRepository.save(technician);
    }
}