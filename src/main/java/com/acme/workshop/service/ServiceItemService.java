package com.acme.workshop.service;

import com.acme.workshop.model.ServiceItem;
import com.acme.workshop.model.ServiceOrder;
import com.acme.workshop.repository.ServiceItemRepository;
import com.acme.workshop.repository.ServiceOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ServiceItemService {
    
    private final ServiceItemRepository serviceItemRepository;
    private final ServiceOrderRepository serviceOrderRepository;

    @Autowired
    public ServiceItemService(ServiceItemRepository serviceItemRepository, 
                             ServiceOrderRepository serviceOrderRepository) {
        this.serviceItemRepository = serviceItemRepository;
        this.serviceOrderRepository = serviceOrderRepository;
    }

    public ServiceItem save(ServiceItem serviceItem) {
        ServiceItem saved = serviceItemRepository.save(serviceItem);
        
        // Atualiza o custo total da ordem de serviço
        ServiceOrder order = saved.getServiceOrder();
        order.updateTotalCost();
        serviceOrderRepository.save(order);
        
        return saved;
    }

    public ServiceItem addServiceItem(Long orderId, ServiceItem serviceItem) {
        ServiceOrder order = serviceOrderRepository.findById(orderId)
            .orElseThrow(() -> new IllegalArgumentException("Ordem de serviço não encontrada"));
        
        serviceItem.setServiceOrder(order);
        return save(serviceItem);
    }

    public List<ServiceItem> findByServiceOrderId(Long serviceOrderId) {
        return serviceItemRepository.findByServiceOrderId(serviceOrderId);
    }

    public ServiceItem findById(Long id) {
        return serviceItemRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Item de serviço não encontrado"));
    }

    @Transactional
    public void deleteById(Long id) {
        ServiceItem item = serviceItemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Item de serviço não encontrado"));

        Long orderId = item.getServiceOrder().getId();

        // Deleta o item
        serviceItemRepository.deleteById(id);
        serviceItemRepository.flush();  // ✅ Commita a deleção

        // Busca a ordem novamente (fresh do banco, sem itens deletados)
        ServiceOrder order = serviceOrderRepository.findById(orderId).orElseThrow();

        // Atualiza o custo total
        order.updateTotalCost();
        serviceOrderRepository.save(order);
    }
}

