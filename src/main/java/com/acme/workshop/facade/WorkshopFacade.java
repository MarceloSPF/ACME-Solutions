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

    // ==========================================
    // SERVICE ORDERS
    // ==========================================

    public List<ServiceOrderResponseDTO> getAllServiceOrders() {
        return serviceOrderService.findAll().stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    @Transactional
    public ServiceOrderResponseDTO createServiceOrder(ServiceOrderRequestDTO requestDTO) {
        Customer customer = customerService.findById(requestDTO.getCustomerId())
            .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado"));

        Vehicle vehicle = vehicleService.findById(requestDTO.getVehicleId())
            .orElseThrow(() -> new IllegalArgumentException("Veículo não encontrado"));

        if (!vehicle.getCustomer().getId().equals(customer.getId())) {
            throw new IllegalArgumentException("Veículo não pertence ao cliente informado");
        }

        Technician technician = technicianService.findById(requestDTO.getTechnicianId())
            .orElseThrow(() -> new IllegalArgumentException("Técnico não encontrado"));

        ServiceOrder serviceOrder = new ServiceOrderBuilder()
            .withCustomer(customer)
            .withVehicle(vehicle)
            .withTechnician(technician)
            .withDescription(requestDTO.getDescription())
            .withTotalCost(requestDTO.getTotalCost() != null ? requestDTO.getTotalCost() : java.math.BigDecimal.ZERO)
            .build();

        serviceOrder = serviceOrderService.save(serviceOrder);
        return convertToDTO(serviceOrder);
    }

    @Transactional
    public ServiceOrderResponseDTO updateServiceOrder(Long id, ServiceOrderRequestDTO dto) {
        ServiceOrder order = serviceOrderService.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Ordem de serviço não encontrada"));
        
        order.setDescription(dto.getDescription());
        order.setTotalCost(dto.getTotalCost());
        
        if (dto.getTechnicianId() != null) {
            Technician tech = technicianService.findById(dto.getTechnicianId())
                .orElseThrow(() -> new IllegalArgumentException("Técnico não encontrado"));
            order.setTechnician(tech);
        }

        // Nota: Geralmente não mudamos Cliente/Veículo numa edição simples de OS, 
        // mas se necessário, adicione a lógica aqui.

        order = serviceOrderService.save(order);
        return convertToDTO(order);
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

    // ==========================================
    // VEHICLES
    // ==========================================

    public List<VehicleDTO> getAllVehicles() {
        return vehicleService.findAll().stream()
            .map(this::convertToVehicleDTO)
            .collect(Collectors.toList());
    }

    public VehicleDTO getVehicle(Long id) {
        Vehicle vehicle = vehicleService.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Veículo não encontrado"));
        return convertToVehicleDTO(vehicle);
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

    @Transactional
    public VehicleDTO updateVehicle(Long id, VehicleDTO vehicleDTO) {
        Vehicle vehicle = vehicleService.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Veículo não encontrado"));
        
        vehicle.setBrand(vehicleDTO.getBrand());
        vehicle.setModel(vehicleDTO.getModel());
        vehicle.setModelYear(vehicleDTO.getModelYear());
        vehicle.setLicensePlate(vehicleDTO.getLicensePlate());
        
        // Atualizar cliente se necessário
        if (vehicleDTO.getCustomerId() != null && !vehicleDTO.getCustomerId().equals(vehicle.getCustomer().getId())) {
             Customer customer = customerService.findById(vehicleDTO.getCustomerId())
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado"));
             vehicle.setCustomer(customer);
        }

        vehicle = vehicleService.save(vehicle);
        return convertToVehicleDTO(vehicle);
    }
    
    public void deleteVehicle(Long id) {
        vehicleService.delete(id);
    }

    public List<VehicleDTO> getVehiclesByCustomer(Long customerId) {
        return vehicleService.findByCustomerId(customerId).stream()
            .map(this::convertToVehicleDTO)
            .collect(Collectors.toList());
    }

    // ==========================================
    // CUSTOMERS
    // ==========================================

    public List<CustomerDTO> getAllCustomers() {
        return customerService.findAll().stream()
            .map(this::convertToCustomerDTO)
            .collect(Collectors.toList());
    }

    public CustomerDTO getCustomer(Long id) {
        Customer customer = customerService.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado"));
        return convertToCustomerDTO(customer);
    }

    @Transactional
    public CustomerDTO createCustomer(CustomerDTO dto) {
        Customer customer = new Customer();
        updateCustomerEntityFromDTO(customer, dto);
        customer = customerService.save(customer);
        return convertToCustomerDTO(customer);
    }

    @Transactional
    public CustomerDTO updateCustomer(Long id, CustomerDTO dto) {
        Customer customer = customerService.update(id, convertToCustomerEntity(dto)); 
        // Nota: O service update já lida com a lógica, mas idealmente passariamos os dados para o service atualizar
        // Como o seu service espera uma Entity, convertemos o DTO para Entity aqui.
        return convertToCustomerDTO(customer);
    }

    public void deleteCustomer(Long id) {
        customerService.delete(id);
    }
    
    public List<CustomerDTO> searchCustomers(String name) {
        return customerService.searchByName(name).stream()
            .map(this::convertToCustomerDTO)
            .collect(Collectors.toList());
    }

    // ==========================================
    // TECHNICIANS
    // ==========================================

    public List<TechnicianDTO> getAllTechnicians() {
        return technicianService.findAll().stream()
            .map(this::convertToTechnicianDTO)
            .collect(Collectors.toList());
    }

    public TechnicianDTO getTechnician(Long id) {
        Technician technician = technicianService.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Técnico não encontrado"));
        return convertToTechnicianDTO(technician);
    }

    @Transactional
    public TechnicianDTO createTechnician(TechnicianDTO dto) {
        Technician technician = new Technician();
        updateTechnicianEntityFromDTO(technician, dto);
        technician = technicianService.save(technician);
        return convertToTechnicianDTO(technician);
    }

    @Transactional
    public TechnicianDTO updateTechnician(Long id, TechnicianDTO dto) {
        Technician technician = technicianService.update(id, convertToTechnicianEntity(dto));
        return convertToTechnicianDTO(technician);
    }

    public void deleteTechnician(Long id) {
        technicianService.delete(id);
    }
    
    public List<TechnicianDTO> searchTechnicians(String specialization) {
        return technicianService.findBySpecialization(specialization).stream()
            .map(this::convertToTechnicianDTO)
            .collect(Collectors.toList());
    }

    // ==========================================
    // PRIVATE CONVERTERS
    // ==========================================

    private ServiceOrderResponseDTO convertToDTO(ServiceOrder serviceOrder) {
        ServiceOrderResponseDTO dto = new ServiceOrderResponseDTO();
        dto.setId(serviceOrder.getId());
        dto.setDescription(serviceOrder.getDescription());
        dto.setStatus(serviceOrder.getStatus());
        dto.setCreatedAt(serviceOrder.getCreatedAt());
        dto.setCompletedAt(serviceOrder.getCompletedAt());
        dto.setTotalCost(serviceOrder.getTotalCost());

        // Convert customer
        dto.setCustomer(convertToCustomerDTO(serviceOrder.getCustomer()));

        // Convert vehicle
        dto.setVehicle(convertToVehicleDTO(serviceOrder.getVehicle()));

        // Convert technician
        dto.setTechnician(convertToTechnicianDTO(serviceOrder.getTechnician()));

        return dto;
    }

    private VehicleDTO convertToVehicleDTO(Vehicle vehicle) {
        VehicleDTO dto = new VehicleDTO();
        dto.setId(vehicle.getId());
        dto.setBrand(vehicle.getBrand());
        dto.setModel(vehicle.getModel());
        dto.setModelYear(vehicle.getModelYear());
        dto.setLicensePlate(vehicle.getLicensePlate());
        if (vehicle.getCustomer() != null) {
            dto.setCustomerId(vehicle.getCustomer().getId());
        }
        return dto;
    }

    private CustomerDTO convertToCustomerDTO(Customer customer) {
        CustomerDTO dto = new CustomerDTO();
        dto.setId(customer.getId());
        dto.setName(customer.getName());
        dto.setEmail(customer.getEmail());
        dto.setPhone(customer.getPhone());
        dto.setAddress(customer.getAddress());
        return dto;
    }

    private Customer convertToCustomerEntity(CustomerDTO dto) {
        Customer customer = new Customer();
        updateCustomerEntityFromDTO(customer, dto);
        return customer;
    }

    private void updateCustomerEntityFromDTO(Customer customer, CustomerDTO dto) {
        customer.setName(dto.getName());
        customer.setEmail(dto.getEmail());
        customer.setPhone(dto.getPhone());
        customer.setAddress(dto.getAddress());
    }

    private TechnicianDTO convertToTechnicianDTO(Technician technician) {
        TechnicianDTO dto = new TechnicianDTO();
        dto.setId(technician.getId());
        dto.setName(technician.getName());
        dto.setEmail(technician.getEmail());
        dto.setSpecialization(technician.getSpecialization());
        return dto;
    }

    private Technician convertToTechnicianEntity(TechnicianDTO dto) {
        Technician technician = new Technician();
        updateTechnicianEntityFromDTO(technician, dto);
        return technician;
    }

    private void updateTechnicianEntityFromDTO(Technician technician, TechnicianDTO dto) {
        technician.setName(dto.getName());
        technician.setEmail(dto.getEmail());
        technician.setSpecialization(dto.getSpecialization());
    }
}