package com.example.product_service.kafka.event;

public record ProductAvailableEvent(
        Long orderId,
        Long productId,
        boolean isAvailable,
        String reason,
        Double price
) {
}
