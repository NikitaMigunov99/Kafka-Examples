package org.example.handler;

import org.example.exception.RetryableException;
import org.example.models.entity.ProcessedEventEntity;
import org.example.models.event.ProductQuantityChangedEvent;
import org.example.repository.ProcessedEventsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class ProductQuantityChangedHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductQuantityChangedHandler.class);

    private final AtomicInteger counter = new AtomicInteger(0);

    private final ProcessedEventsRepository repository;

    public ProductQuantityChangedHandler(ProcessedEventsRepository repository) {
        this.repository = repository;
    }

    @KafkaListener(topics = "product-quantity-changed-events-topic", groupId = "product-quantity-changed")
    public void handleEvent(@Payload ProductQuantityChangedEvent event,
                            @Header("requestId") String requestId,
                            @Header(KafkaHeaders.RECEIVED_KEY) String receivedKey) {
        if (receivedKey != null) {
            LOGGER.info("Received key: {}", receivedKey);
        }
        if (requestId != null) {
            Optional<ProcessedEventEntity> processedEventEntity = repository.findByRequestId(requestId);
            if (processedEventEntity.isPresent()) {
                LOGGER.info("Event already processed. Request id is {}, Event is: {}", requestId, event);
            }
        }
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        String time = now.format(formatter);
        LOGGER.info("Processing event with id: {}, thread: {}, time: {}", event.getProductId(), Thread.currentThread().getId(), time);
        if (event.getProductId().equals("5") && counter.incrementAndGet() < 50) {
            LOGGER.info("Exception for id: 5, counter: {}", counter.get());
            throw new RetryableException("Exception thrown");
        }

        if (requestId != null) {
            ProcessedEventEntity entity = new ProcessedEventEntity(requestId, "processed");
            repository.save(entity);
        }
        LOGGER.info("Event handled. Event is: {}", event);
    }
}
