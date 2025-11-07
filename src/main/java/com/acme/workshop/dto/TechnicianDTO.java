package com.acme.workshop.dto;

import java.io.Serializable;

public class TechnicianDTO implements Serializable {
    private Long id;
    private String name;
    private String email;
    private String specialization;

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }
}