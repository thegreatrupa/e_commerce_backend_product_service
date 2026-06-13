package com.example.product_service.kafka.event;

public record OrderCreateEvent(
        Long orderId,
        Long productId,
        Integer quantity,
        Long userId
) {
}
