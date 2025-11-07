package com.acme.workshop.service.observer;

import com.acme.workshop.model.ServiceOrder;
import org.springframework.stereotype.Component;

@Component
public class EmailNotificationObserver implements ServiceOrderObserver {
    
    @Override
    public void onServiceOrderStatusChange(ServiceOrder serviceOrder, ServiceOrder.ServiceStatus oldStatus) {
        // Simulando o envio de e-mail (em um sistema real, isso seria injetado via EmailService)
        String customerEmail = serviceOrder.getCustomer().getEmail();
        String message = String.format(
            "Prezado cliente, sua ordem de serviço #%d teve seu status alterado de %s para %s.",
            serviceOrder.getId(),
            oldStatus,
            serviceOrder.getStatus()
        );
        
        System.out.println("Enviando e-mail para: " + customerEmail);
        System.out.println("Mensagem: " + message);
    }
}