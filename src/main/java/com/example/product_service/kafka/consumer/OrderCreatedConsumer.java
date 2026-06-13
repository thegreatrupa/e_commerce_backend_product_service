package com.example.product_service.kafka.consumer;

import com.example.product_service.kafka.event.OrderCreateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;

public class OrderCreatedConsumer {
    private static final Logger log = LoggerFactory.getLogger(OrderCreatedConsumer.class);

    @KafkaListener(
            topics = "order-created",
            groupId = "product-group"
    )
    public void consume(OrderCreateEvent event){
        log.info("Received order event. orderId={}, productId={}", event.orderId(), event.productId());
    }
}
