package org.example.service;

import org.apache.kafka.clients.producer.RecordMetadata;
import org.example.models.CreateProductDTO;
import org.example.models.event.CreateProductEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CreateProductService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CreateProductService.class);
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public CreateProductService(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public String createProduct(CreateProductDTO productDTO) {
        String id = UUID.randomUUID().toString();
        var event = new CreateProductEvent(id, productDTO.title(), productDTO.price(), productDTO.quantity());

        kafkaTemplate.send("product-created-events-topic", id, event)
                .whenComplete((sendResult, throwable) -> {
                            if (throwable != null) {
                                LOGGER.error("Error sending message", throwable);
                            } else {
                                RecordMetadata metadata = sendResult.getRecordMetadata();
                                LOGGER.info("Topic: {}", metadata.topic());
                                LOGGER.info("Partition: {}", metadata.partition());
                                LOGGER.info("Offset: {}", metadata.offset());
                            }
                        }
                );
        LOGGER.info("Created product with id: {}", id);
        return id;
    }
}
