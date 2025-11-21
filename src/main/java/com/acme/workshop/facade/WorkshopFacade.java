package com.acme.workshop.facade;

import com.acme.workshop.dto.*;
import com.acme.workshop.model.*;
import com.acme.workshop.repository.PartRepository;
import com.acme.workshop.service.*;
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
    private final PartRepository partRepository;
    private final WorkServiceService workServiceService;

    public WorkshopFacade(
            CustomerService customerService,
            TechnicianService technicianService,
            VehicleService vehicleService,
            ServiceOrderService serviceOrderService,
            PartRepository partRepository,
            WorkServiceService workServiceService) {
        this.customerService = customerService;
        this.technicianService = technicianService;
        this.vehicleService = vehicleService;
        this.serviceOrderService = serviceOrderService;
        this.partRepository = partRepository;
        this.workServiceService = workServiceService;
    }

    // ==========================================
    // SERVICE ORDERS
    // ==========================================

    @Transactional(readOnly = true)
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
        Technician technician = technicianService.findById(requestDTO.getTechnicianId())
            .orElseThrow(() -> new IllegalArgumentException("Técnico não encontrado"));

        ServiceOrder serviceOrder = new ServiceOrderBuilder()
            .withCustomer(customer)
            .withVehicle(vehicle)
            .withTechnician(technician)
            .withDescription(requestDTO.getDescription())
            .withTotalCost(java.math.BigDecimal.ZERO)
            .build();

        // Salvar Itens
        if (requestDTO.getServiceItems() != null) {
            List<ServiceItem> items = requestDTO.getServiceItems().stream().map(itemDto -> {
                ServiceItem item = new ServiceItem();
                item.setDescription(itemDto.getDescription());
                item.setLaborCost(itemDto.getLaborCost());
                item.setQuantity(itemDto.getQuantity());
                item.setServiceOrder(serviceOrder);
                return item;
            }).collect(Collectors.toList());
            serviceOrder.setServiceItems(items);
        }

        // Salvar Peças
        if (requestDTO.getParts() != null) {
            List<ServiceOrderPart> parts = requestDTO.getParts().stream().map(partDto -> {
                Part part = partRepository.findById(partDto.getPartId())
                    .orElseThrow(() -> new IllegalArgumentException("Peça não encontrada ID: " + partDto.getPartId()));
                
                if (part.getStock() < partDto.getQuantity()) {
                    throw new IllegalArgumentException("Estoque insuficiente: " + part.getName());
                }
                part.setStock(part.getStock() - partDto.getQuantity());
                partRepository.save(part);

                ServiceOrderPart orderPart = new ServiceOrderPart();
                orderPart.setPart(part);
                orderPart.setQuantity(partDto.getQuantity());
                orderPart.setUnitPrice(part.getUnitPrice());
                orderPart.setServiceOrder(serviceOrder);
                return orderPart;
            }).collect(Collectors.toList());
            serviceOrder.setParts(parts);
        }

        serviceOrder.updateTotalCost();
        ServiceOrder savedOrder = serviceOrderService.save(serviceOrder);
        return convertToDTO(savedOrder);
    }

    @Transactional
    public ServiceOrderResponseDTO updateServiceOrder(Long id, ServiceOrderRequestDTO dto) {
        ServiceOrder order = serviceOrderService.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("OS não encontrada"));
        
        order.setDescription(dto.getDescription());
        
        if (dto.getTechnicianId() != null) {
            order.setTechnician(technicianService.findById(dto.getTechnicianId()).orElseThrow());
        }

        if (dto.getStatus() != null && dto.getStatus() != order.getStatus()) {
            order = serviceOrderService.updateStatus(order.getId(), dto.getStatus());
        }

        final ServiceOrder orderRef = order;

        if (dto.getServiceItems() != null) {
            order.getServiceItems().clear();
            order.getServiceItems().addAll(dto.getServiceItems().stream().map(i -> {
                ServiceItem item = new ServiceItem();
                item.setDescription(i.getDescription());
                item.setLaborCost(i.getLaborCost());
                item.setQuantity(i.getQuantity());
                item.setServiceOrder(orderRef);
                return item;
            }).collect(Collectors.toList()));
        }

        if (dto.getParts() != null) {
            order.getParts().forEach(p -> {
                p.getPart().setStock(p.getPart().getStock() + p.getQuantity());
                partRepository.save(p.getPart());
            });
            order.getParts().clear();

            order.getParts().addAll(dto.getParts().stream().map(pDto -> {
                Part part = partRepository.findById(pDto.getPartId()).orElseThrow();
                part.setStock(part.getStock() - pDto.getQuantity());
                partRepository.save(part);

                ServiceOrderPart sp = new ServiceOrderPart();
                sp.setPart(part);
                sp.setQuantity(pDto.getQuantity());
                sp.setUnitPrice(part.getUnitPrice());
                sp.setServiceOrder(orderRef);
                return sp;
            }).collect(Collectors.toList()));
        }

        order.updateTotalCost();
        return convertToDTO(serviceOrderService.save(order));
    }

    @Transactional
    public ServiceOrderResponseDTO updateServiceOrderStatus(Long orderId, ServiceOrder.ServiceStatus newStatus) {
        return convertToDTO(serviceOrderService.updateStatus(orderId, newStatus));
    }

    public List<ServiceOrderResponseDTO> getCustomerServiceOrders(Long customerId) {
        return serviceOrderService.findByCustomerId(customerId).stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public List<ServiceOrderResponseDTO> getTechnicianServiceOrders(Long technicianId) {
        return serviceOrderService.findByTechnicianId(technicianId).stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    // --- CONVERSOR PRINCIPAL ---
    private ServiceOrderResponseDTO convertToDTO(ServiceOrder serviceOrder) {
        ServiceOrderResponseDTO dto = new ServiceOrderResponseDTO();
        dto.setId(serviceOrder.getId());
        dto.setDescription(serviceOrder.getDescription());
        dto.setStatus(serviceOrder.getStatus());
        dto.setCreatedAt(serviceOrder.getCreatedAt());
        dto.setCompletedAt(serviceOrder.getCompletedAt());
        dto.setTotalCost(serviceOrder.getTotalCost());

        dto.setCustomer(convertToCustomerDTO(serviceOrder.getCustomer()));
        dto.setVehicle(convertToVehicleDTO(serviceOrder.getVehicle()));
        dto.setTechnician(convertToTechnicianDTO(serviceOrder.getTechnician()));

        // Converter Listas de Serviços (CORRIGIDO PARA INCLUIR ID E LISTA)
        if (serviceOrder.getServiceItems() != null) {
            dto.setServiceItems(serviceOrder.getServiceItems().stream().map(item -> {
                ServiceItemDTO iDto = new ServiceItemDTO();
                iDto.setId(item.getId());
                iDto.setServiceOrderId(serviceOrder.getId()); // ID da OS
                iDto.setDescription(item.getDescription());
                iDto.setLaborCost(item.getLaborCost());
                iDto.setQuantity(item.getQuantity());
                iDto.setTotalCost(item.getTotalCost());
                return iDto;
            }).collect(Collectors.toList()));
        }

        // Converter Listas de Peças (CORRIGIDO PARA INCLUIR ID E LISTA)
        if (serviceOrder.getParts() != null) {
            dto.setParts(serviceOrder.getParts().stream().map(part -> {
                ServiceOrderPartDTO pDto = new ServiceOrderPartDTO();
                pDto.setId(part.getId());
                pDto.setServiceOrderId(serviceOrder.getId()); // ID da OS
                pDto.setPartId(part.getPart().getId());
                pDto.setPartName(part.getPart().getName());
                pDto.setPartCode(part.getPart().getCode());
                pDto.setQuantity(part.getQuantity());
                pDto.setUnitPrice(part.getUnitPrice());
                pDto.setTotalCost(part.getTotalCost());
                return pDto;
            }).collect(Collectors.toList()));
        }
        return dto;
    }

    // ... (Mantidos os outros métodos auxiliares: WorkService, CRUDs, etc) ...
    
    private VehicleDTO convertToVehicleDTO(Vehicle vehicle) {
        VehicleDTO dto = new VehicleDTO();
        dto.setId(vehicle.getId());
        dto.setBrand(vehicle.getBrand());
        dto.setModel(vehicle.getModel());
        dto.setModelYear(vehicle.getModelYear());
        dto.setLicensePlate(vehicle.getLicensePlate());
        if (vehicle.getCustomer() != null) {
            dto.setCustomerId(vehicle.getCustomer().getId());
            dto.setCustomerName(vehicle.getCustomer().getName());
        }
        return dto;
    }
    private CustomerDTO convertToCustomerDTO(Customer customer) {
        CustomerDTO dto = new CustomerDTO();
        dto.setId(customer.getId()); dto.setName(customer.getName()); dto.setEmail(customer.getEmail()); dto.setPhone(customer.getPhone()); dto.setAddress(customer.getAddress());
        return dto;
    }
    private TechnicianDTO convertToTechnicianDTO(Technician technician) {
        TechnicianDTO dto = new TechnicianDTO();
        dto.setId(technician.getId()); dto.setName(technician.getName()); dto.setEmail(technician.getEmail()); dto.setSpecialization(technician.getSpecialization());
        return dto;
    }
    
    public List<WorkServiceDTO> getAllWorkServices() { return workServiceService.findAll().stream().map(this::convertToWorkServiceDTO).collect(Collectors.toList()); }
    public WorkServiceDTO createWorkService(WorkServiceDTO dto) {
        WorkService entity = new WorkService(); entity.setDescription(dto.getDescription()); entity.setStandardPrice(dto.getStandardPrice());
        return convertToWorkServiceDTO(workServiceService.save(entity));
    }
    public WorkServiceDTO updateWorkService(Long id, WorkServiceDTO dto) {
        WorkService entity = new WorkService(); entity.setDescription(dto.getDescription()); entity.setStandardPrice(dto.getStandardPrice());
        return convertToWorkServiceDTO(workServiceService.update(id, entity));
    }
    public void deleteWorkService(Long id) { workServiceService.delete(id); }
    private WorkServiceDTO convertToWorkServiceDTO(WorkService s) {
        WorkServiceDTO dto = new WorkServiceDTO(); dto.setId(s.getId()); dto.setDescription(s.getDescription()); dto.setStandardPrice(s.getStandardPrice()); return dto;
    }
    
    public List<CustomerDTO> getAllCustomers() { return customerService.findAll().stream().map(this::convertToCustomerDTO).collect(Collectors.toList()); }
    public CustomerDTO getCustomer(Long id) { return convertToCustomerDTO(customerService.findById(id).orElseThrow()); }
    @Transactional public CustomerDTO createCustomer(CustomerDTO dto) { Customer c = new Customer(); c.setName(dto.getName()); c.setEmail(dto.getEmail()); c.setPhone(dto.getPhone()); c.setAddress(dto.getAddress()); return convertToCustomerDTO(customerService.save(c)); }
    @Transactional public CustomerDTO updateCustomer(Long id, CustomerDTO dto) { Customer c = new Customer(); c.setName(dto.getName()); c.setEmail(dto.getEmail()); c.setPhone(dto.getPhone()); c.setAddress(dto.getAddress()); return convertToCustomerDTO(customerService.update(id, c)); }
    public void deleteCustomer(Long id) { customerService.delete(id); }
    public List<CustomerDTO> searchCustomers(String s) { return customerService.searchByName(s).stream().map(this::convertToCustomerDTO).collect(Collectors.toList()); }

    public List<VehicleDTO> getAllVehicles() { return vehicleService.findAll().stream().map(this::convertToVehicleDTO).collect(Collectors.toList()); }
    public VehicleDTO getVehicle(Long id) { return convertToVehicleDTO(vehicleService.findById(id).orElseThrow()); }
    @Transactional public VehicleDTO createVehicle(VehicleDTO dto) { 
        Vehicle v = new Vehicle(); v.setBrand(dto.getBrand()); v.setModel(dto.getModel()); v.setModelYear(dto.getModelYear()); v.setLicensePlate(dto.getLicensePlate());
        v.setCustomer(customerService.findById(dto.getCustomerId()).orElseThrow());
        return convertToVehicleDTO(vehicleService.save(v)); 
    }
    @Transactional public VehicleDTO updateVehicle(Long id, VehicleDTO dto) {
        Vehicle v = vehicleService.findById(id).orElseThrow(); v.setBrand(dto.getBrand()); v.setModel(dto.getModel()); v.setModelYear(dto.getModelYear()); v.setLicensePlate(dto.getLicensePlate());
        return convertToVehicleDTO(vehicleService.save(v));
    }
    public void deleteVehicle(Long id) { vehicleService.delete(id); }
    public List<VehicleDTO> getVehiclesByCustomer(Long id) { return vehicleService.findByCustomerId(id).stream().map(this::convertToVehicleDTO).collect(Collectors.toList()); }

    public List<TechnicianDTO> getAllTechnicians() { return technicianService.findAll().stream().map(this::convertToTechnicianDTO).collect(Collectors.toList()); }
    public TechnicianDTO getTechnician(Long id) { return convertToTechnicianDTO(technicianService.findById(id).orElseThrow()); }
    @Transactional public TechnicianDTO createTechnician(TechnicianDTO dto) { Technician t = new Technician(); t.setName(dto.getName()); t.setEmail(dto.getEmail()); t.setSpecialization(dto.getSpecialization()); return convertToTechnicianDTO(technicianService.save(t)); }
    @Transactional public TechnicianDTO updateTechnician(Long id, TechnicianDTO dto) { Technician t = new Technician(); t.setName(dto.getName()); t.setEmail(dto.getEmail()); t.setSpecialization(dto.getSpecialization()); return convertToTechnicianDTO(technicianService.update(id, t)); }
    public void deleteTechnician(Long id) { technicianService.delete(id); }
    public List<TechnicianDTO> searchTechnicians(String s) { return technicianService.findBySpecialization(s).stream().map(this::convertToTechnicianDTO).collect(Collectors.toList()); }
}