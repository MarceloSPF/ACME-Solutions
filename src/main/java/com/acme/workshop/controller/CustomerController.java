package com.acme.workshop.controller;

import com.acme.workshop.dto.CustomerDTO;
import com.acme.workshop.dto.CustomerSelectDTO;
import com.acme.workshop.facade.WorkshopFacade;
import com.acme.workshop.model.Customer;
import com.acme.workshop.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {
    
    private final WorkshopFacade workshopFacade;

    private final CustomerService customerService;

    public CustomerController(WorkshopFacade workshopFacade, CustomerService customerService) {
        this.workshopFacade = workshopFacade;
        this.customerService = customerService;
    }

    @PostMapping
    public ResponseEntity<CustomerDTO> createCustomer(@Valid @RequestBody CustomerDTO customerDTO) {
        Customer customer = convertToEntity(customerDTO);
        Customer savedCustomer = customerService.save(customer);
        CustomerDTO responseDTO = convertToDTO(savedCustomer);
        
        URI location = ServletUriComponentsBuilder
            .fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(savedCustomer.getId())
            .toUri();

        return ResponseEntity.created(location).body(responseDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerDTO> getCustomer(@PathVariable Long id) {
        return customerService.findById(id)
            .map(this::convertToDTO)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<CustomerDTO> getAllCustomers() {
        return customerService.findAll().stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerDTO> updateCustomer(
            @PathVariable Long id,
            @Valid @RequestBody CustomerDTO customerDTO) {
        Customer customer = convertToEntity(customerDTO);
        Customer updatedCustomer = customerService.update(id, customer);
        return ResponseEntity.ok(convertToDTO(updatedCustomer));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        customerService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public List<CustomerDTO> searchCustomers(@RequestParam String name) {
        return customerService.searchByName(name).stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    private CustomerDTO convertToDTO(Customer customer) {
        CustomerDTO dto = new CustomerDTO();
        dto.setId(customer.getId());
        dto.setName(customer.getName());
        dto.setEmail(customer.getEmail());
        dto.setPhone(customer.getPhone());
        dto.setAddress(customer.getAddress());
        return dto;
    }

    private Customer convertToEntity(CustomerDTO dto) {
        Customer customer = new Customer();
        customer.setId(dto.getId());
        customer.setName(dto.getName());
        customer.setEmail(dto.getEmail());
        customer.setPhone(dto.getPhone());
        customer.setAddress(dto.getAddress());
        return customer;
    }
    @GetMapping("/select") // Endpoint: /api/customers/select
    public ResponseEntity<List<CustomerSelectDTO>> getCustomersForDropdown() {
        return ResponseEntity.ok(workshopFacade.getCustomersForSelect());
    }
}