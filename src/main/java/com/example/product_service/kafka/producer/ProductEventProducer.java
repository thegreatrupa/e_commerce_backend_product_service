package com.example.product_service.kafka.producer;

import com.example.product_service.kafka.event.ProductAvailableEvent;
import com.example.product_service.kafka.event.ProductUnavailableEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class ProductEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public ProductEventProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendProductAvailable(ProductAvailableEvent event){
        kafkaTemplate.send(
                "product-available",
                event.orderId().toString(),
                event
        );
    }
}
