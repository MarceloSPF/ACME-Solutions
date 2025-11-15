package com.acme.workshop.service;

import com.acme.workshop.model.Part;
import com.acme.workshop.repository.PartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PartService {
    
    private final PartRepository partRepository;

    @Autowired
    public PartService(PartRepository partRepository) {
        this.partRepository = partRepository;
    }

    public Part save(Part part) {
        return partRepository.save(part);
    }

    public Optional<Part> findById(Long id) {
        return partRepository.findById(id);
    }

    public List<Part> findAll() {
        return partRepository.findAll();
    }

    public Optional<Part> findByCode(String code) {
        return partRepository.findByCode(code);
    }

    public boolean existsByCode(String code) {
        return partRepository.existsByCode(code);
    }

    public void deleteById(Long id) {
        partRepository.deleteById(id);
    }

    public Part updateStock(Long id, Integer quantity) {
        Part part = partRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Peça não encontrada"));
        
        int newStock = part.getStock() - quantity;
        if (newStock < 0) {
            throw new IllegalArgumentException("Estoque insuficiente. Disponível: " + part.getStock());
        }
        
        part.setStock(newStock);
        return partRepository.save(part);
    }
}

