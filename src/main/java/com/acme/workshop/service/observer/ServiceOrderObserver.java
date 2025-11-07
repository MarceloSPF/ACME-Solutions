package com.acme.workshop.service.observer;

import com.acme.workshop.model.ServiceOrder;

public interface ServiceOrderObserver {
    void onServiceOrderStatusChange(ServiceOrder serviceOrder, ServiceOrder.ServiceStatus oldStatus);
}