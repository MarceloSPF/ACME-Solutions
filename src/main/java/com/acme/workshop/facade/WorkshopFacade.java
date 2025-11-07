package com.acme.workshop.facade;

import com.acme.workshop.dto.ServiceOrderRequestDTO;
import com.acme.workshop.dto.ServiceOrderResponseDTO;
import com.acme.workshop.dto.VehicleDTO;
import com.acme.workshop.dto.CustomerDTO;
import com.acme.workshop.dto.TechnicianDTO;
import com.acme.workshop.model.*;
import com.acme.workshop.service.CustomerService;
import com.acme.workshop.service.TechnicianService;
import com.acme.workshop.service.ServiceOrderService;
import com.acme.workshop.service.VehicleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class WorkshopFacade {
    
    private final CustomerService customerService;
    private final TechnicianService technicianService;
    private final VehicleService vehicleService;
    private final ServiceOrderService serviceOrderService;

    public WorkshopFacade(
            CustomerService customerService,
            TechnicianService technicianService,
            VehicleService vehicleService,
            ServiceOrderService serviceOrderService) {
        this.customerService = customerService;
        this.technicianService = technicianService;
        this.vehicleService = vehicleService;
        this.serviceOrderService = serviceOrderService;
    }

    @Transactional
    public ServiceOrderResponseDTO createServiceOrder(ServiceOrderRequestDTO requestDTO) {
        // Validar e obter todas as entidades necessárias
        Customer customer = customerService.findById(requestDTO.getCustomerId())
            .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado"));

        Vehicle vehicle = vehicleService.findById(requestDTO.getVehicleId())
            .orElseThrow(() -> new IllegalArgumentException("Veículo não encontrado"));

        if (!vehicle.getCustomer().getId().equals(customer.getId())) {
            throw new IllegalArgumentException("Veículo não pertence ao cliente informado");
        }

        Technician technician = technicianService.findById(requestDTO.getTechnicianId())
            .orElseThrow(() -> new IllegalArgumentException("Técnico não encontrado"));

        // Criar a ordem de serviço
        ServiceOrder serviceOrder = new ServiceOrderBuilder()
            .withCustomer(customer)
            .withVehicle(vehicle)
            .withTechnician(technician)
            .withDescription(requestDTO.getDescription())
            .withTotalCost(requestDTO.getTotalCost())
            .build();

        serviceOrder = serviceOrderService.save(serviceOrder);
        return convertToDTO(serviceOrder);
    }

    @Transactional
    public ServiceOrderResponseDTO updateServiceOrderStatus(Long orderId, ServiceOrder.ServiceStatus newStatus) {
        ServiceOrder updatedOrder = serviceOrderService.updateStatus(orderId, newStatus);
        return convertToDTO(updatedOrder);
    }

    public List<ServiceOrderResponseDTO> getCustomerServiceOrders(Long customerId) {
        return serviceOrderService.findByCustomerId(customerId).stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    public List<ServiceOrderResponseDTO> getTechnicianServiceOrders(Long technicianId) {
        return serviceOrderService.findByTechnicianId(technicianId).stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    @Transactional
    public VehicleDTO createVehicle(VehicleDTO vehicleDTO) {
        Customer customer = customerService.findById(vehicleDTO.getCustomerId())
            .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado"));
        
        Vehicle vehicle = new Vehicle();
        vehicle.setBrand(vehicleDTO.getBrand());
        vehicle.setModel(vehicleDTO.getModel());
        vehicle.setModelYear(vehicleDTO.getModelYear());
        vehicle.setLicensePlate(vehicleDTO.getLicensePlate());
        vehicle.setCustomer(customer);

        vehicle = vehicleService.save(vehicle);
        return convertToVehicleDTO(vehicle);
    }

    public VehicleDTO getVehicle(Long id) {
        Vehicle vehicle = vehicleService.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Veículo não encontrado"));
        return convertToVehicleDTO(vehicle);
    }

    public List<VehicleDTO> getAllVehicles() {
        return vehicleService.findAll().stream()
            .map(this::convertToVehicleDTO)
            .collect(Collectors.toList());
    }

    public List<VehicleDTO> getVehiclesByCustomer(Long customerId) {
        return vehicleService.findByCustomerId(customerId).stream()
            .map(this::convertToVehicleDTO)
            .collect(Collectors.toList());
    }

    private ServiceOrderResponseDTO convertToDTO(ServiceOrder serviceOrder) {
        ServiceOrderResponseDTO dto = new ServiceOrderResponseDTO();
        dto.setId(serviceOrder.getId());
        dto.setDescription(serviceOrder.getDescription());
        dto.setStatus(serviceOrder.getStatus());
        dto.setCreatedAt(serviceOrder.getCreatedAt());
        dto.setCompletedAt(serviceOrder.getCompletedAt());
        dto.setTotalCost(serviceOrder.getTotalCost());

        // Convert customer
        CustomerDTO customerDTO = new CustomerDTO();
        customerDTO.setId(serviceOrder.getCustomer().getId());
        customerDTO.setName(serviceOrder.getCustomer().getName());
        customerDTO.setEmail(serviceOrder.getCustomer().getEmail());
        customerDTO.setPhone(serviceOrder.getCustomer().getPhone());
        customerDTO.setAddress(serviceOrder.getCustomer().getAddress());
        dto.setCustomer(customerDTO);

        // Convert vehicle
        VehicleDTO vehicleDTO = convertToVehicleDTO(serviceOrder.getVehicle());
        dto.setVehicle(vehicleDTO);

        // Convert technician
        TechnicianDTO technicianDTO = new TechnicianDTO();
        technicianDTO.setId(serviceOrder.getTechnician().getId());
        technicianDTO.setName(serviceOrder.getTechnician().getName());
        technicianDTO.setEmail(serviceOrder.getTechnician().getEmail());
        technicianDTO.setSpecialization(serviceOrder.getTechnician().getSpecialization());
        dto.setTechnician(technicianDTO);

        return dto;
    }

    private VehicleDTO convertToVehicleDTO(Vehicle vehicle) {
        VehicleDTO dto = new VehicleDTO();
        dto.setId(vehicle.getId());
        dto.setBrand(vehicle.getBrand());
        dto.setModel(vehicle.getModel());
        dto.setModelYear(vehicle.getModelYear());
        dto.setLicensePlate(vehicle.getLicensePlate());
        dto.setCustomerId(vehicle.getCustomer().getId());
        return dto;
    }
}