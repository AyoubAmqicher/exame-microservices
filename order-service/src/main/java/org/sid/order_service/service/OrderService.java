package org.sid.order_service.service;


import org.sid.order_service.entities.Order;

public interface OrderService {
    Order createOrder(Order order);
}
