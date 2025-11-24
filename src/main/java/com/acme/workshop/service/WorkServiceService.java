package com.acme.workshop.service;

import com.acme.workshop.model.WorkService;
import com.acme.workshop.repository.WorkServiceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class WorkServiceService {
    private final WorkServiceRepository repository;

    public WorkServiceService(WorkServiceRepository repository) {
        this.repository = repository;
    }

    public List<WorkService> findAll() { return repository.findAll(); }
    public WorkService findById(Long id) { return repository.findById(id).orElseThrow(); }
    public WorkService save(WorkService s) { return repository.save(s); }
    public void delete(Long id) { repository.deleteById(id); }
    
    public WorkService update(Long id, WorkService s) {
        WorkService existing = findById(id);
        existing.setDescription(s.getDescription());
        existing.setStandardPrice(s.getStandardPrice());
        return repository.save(existing);
    }
}