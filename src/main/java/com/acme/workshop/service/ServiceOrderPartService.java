package com.acme.workshop.service;

import com.acme.workshop.model.Part;
import com.acme.workshop.model.ServiceOrder;
import com.acme.workshop.model.ServiceOrderPart;
import com.acme.workshop.repository.ServiceOrderPartRepository;
import com.acme.workshop.repository.ServiceOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ServiceOrderPartService {
    
    private final ServiceOrderPartRepository serviceOrderPartRepository;
    private final ServiceOrderRepository serviceOrderRepository;
    private final PartService partService;

    @Autowired
    public ServiceOrderPartService(ServiceOrderPartRepository serviceOrderPartRepository,
                                   ServiceOrderRepository serviceOrderRepository,
                                   PartService partService) {
        this.serviceOrderPartRepository = serviceOrderPartRepository;
        this.serviceOrderRepository = serviceOrderRepository;
        this.partService = partService;
    }

    public ServiceOrderPart addPartToOrder(Long orderId, Long partId, Integer quantity) {
        ServiceOrder order = serviceOrderRepository.findById(orderId)
            .orElseThrow(() -> new IllegalArgumentException("Ordem de serviço não encontrada"));
        
        Part part = partService.findById(partId)
            .orElseThrow(() -> new IllegalArgumentException("Peça não encontrada"));
        
        // Verificar estoque
        if (part.getStock() < quantity) {
            throw new IllegalArgumentException(
                "Estoque insuficiente. Disponível: " + part.getStock() + ", Solicitado: " + quantity
            );
        }
        
        // Criar associação
        ServiceOrderPart orderPart = new ServiceOrderPart();
        orderPart.setServiceOrder(order);
        orderPart.setPart(part);
        orderPart.setQuantity(quantity);
        orderPart.setUnitPrice(part.getUnitPrice()); // Snapshot do preço
        
        ServiceOrderPart saved = serviceOrderPartRepository.save(orderPart);
        
        // Atualizar estoque
        partService.updateStock(partId, quantity);
        
        // Atualizar custo total da ordem
        order.updateTotalCost();
        serviceOrderRepository.save(order);
        
        return saved;
    }

    public List<ServiceOrderPart> findByServiceOrderId(Long serviceOrderId) {
        return serviceOrderPartRepository.findByServiceOrderId(serviceOrderId);
    }

    public void removePartFromOrder(Long orderPartId) {
        ServiceOrderPart orderPart = serviceOrderPartRepository.findById(orderPartId)
            .orElseThrow(() -> new IllegalArgumentException("Associação não encontrada"));
        
        ServiceOrder order = orderPart.getServiceOrder();
        Part part = orderPart.getPart();
        Integer quantity = orderPart.getQuantity();
        
        // Remover associação
        serviceOrderPartRepository.deleteById(orderPartId);
        
        // Devolver ao estoque
        part.setStock(part.getStock() + quantity);
        partService.save(part);
        
        // Atualizar custo total da ordem
        order.updateTotalCost();
        serviceOrderRepository.save(order);
    }

    public ServiceOrderPart updateQuantity(Long orderPartId, Integer newQuantity) {
        ServiceOrderPart orderPart = serviceOrderPartRepository.findById(orderPartId)
            .orElseThrow(() -> new IllegalArgumentException("Associação não encontrada"));
        
        Part part = orderPart.getPart();
        Integer oldQuantity = orderPart.getQuantity();
        int quantityDifference = newQuantity - oldQuantity;
        
        // Verificar estoque se estiver aumentando a quantidade
        if (quantityDifference > 0 && part.getStock() < quantityDifference) {
            throw new IllegalArgumentException(
                "Estoque insuficiente. Disponível: " + part.getStock() + ", Necessário: " + quantityDifference
            );
        }
        
        // Atualizar quantidade
        orderPart.setQuantity(newQuantity);
        ServiceOrderPart saved = serviceOrderPartRepository.save(orderPart);
        
        // Atualizar estoque
        if (quantityDifference != 0) {
            partService.updateStock(part.getId(), -quantityDifference);
        }
        
        // Atualizar custo total da ordem
        ServiceOrder order = orderPart.getServiceOrder();
        order.updateTotalCost();
        serviceOrderRepository.save(order);
        
        return saved;
    }
}

