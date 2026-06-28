package com.example.product_service.kafka.event;

public record ProductUnavailableEvent(
        Long orderId,
        Long productId,
        String reason
) {
}
