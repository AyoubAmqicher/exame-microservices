package org.sid.order_service.service;

import org.sid.order_service.entities.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class OrderEventPublisher {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    private static final String TOPIC = "orders-topic";

    public void sendOrderEvent(Order order) {
        kafkaTemplate.send(TOPIC, order.toString());
    }
}
