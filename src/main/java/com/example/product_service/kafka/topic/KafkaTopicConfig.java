package com.example.product_service.kafka.topic;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic ProductAvailableTopic(){
        return TopicBuilder
                .name("product-available")
                .partitions(1)
                .replicas(1)
                .build();
    }

}
