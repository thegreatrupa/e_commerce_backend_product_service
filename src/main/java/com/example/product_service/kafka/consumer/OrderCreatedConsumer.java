package com.example.product_service.kafka.consumer;

import com.example.product_service.entity.Product;
import com.example.product_service.kafka.event.OrderCreateEvent;
import com.example.product_service.kafka.event.ProductAvailableEvent;
import com.example.product_service.kafka.event.ProductUnavailableEvent;
import com.example.product_service.kafka.producer.ProductEventProducer;
import com.example.product_service.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class OrderCreatedConsumer {
    private static final Logger log = LoggerFactory.getLogger(OrderCreatedConsumer.class);
    private final ProductRepository productRepository;
    private final ProductEventProducer producer;

    public OrderCreatedConsumer(ProductRepository productRepository, ProductEventProducer producer) {
        this.productRepository = productRepository;
        this.producer = producer;
    }

    @KafkaListener(
            topics = "order-created",
            groupId = "product-service-group"
    )

    public void consume(OrderCreateEvent event){
        log.info("Received order event. orderId={}, productId={}", event.orderId(), event.productId());
        Product product = productRepository.findById(event.productId()).orElse(null);

        if(product == null || product.getStock() < event.quantity()){
            producer.sendProductAvailable(
                    new ProductAvailableEvent(
                            event.orderId(),
                            event.productId(),
                            false,
                            "out of stock",
                            0.0
                    )
            );
            return;
        }

        product.setStock(product.getStock() - event.quantity());
        productRepository.save(product);

        producer.sendProductAvailable(
                new ProductAvailableEvent(
                        event.orderId(),
                        event.productId(),
                        true,
                        "",
                        product.getPrice()
                )
        );
    }
}
