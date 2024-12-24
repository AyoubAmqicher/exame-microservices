package org.sid.customer_service.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class OrderEventListener {

    @KafkaListener(topics = "orders-topic", groupId = "notification_group")
    public void consumeOrderEvent(String orderEvent) {
        System.out.println("Received order event: " + orderEvent);
        // Add logic to send notifications
    }
}