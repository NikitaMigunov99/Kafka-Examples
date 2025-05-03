package org.example.handler;

import org.example.exception.RetryableException;
import org.example.models.event.ProductQuantityChangedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

@Component
public class ProductQuantityChangedHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductQuantityChangedHandler.class);

    private final AtomicInteger counter = new AtomicInteger(0);

    @KafkaListener(topics = "product-quantity-changed-events-topic", groupId = "product-quantity-changed")
    public void handleEvent(ProductQuantityChangedEvent event) {
        LOGGER.info("Processing event with id: {}, thread: {}", event.getProductId(), Thread.currentThread().getId());
        if (event.getProductId().equals("5") && counter.incrementAndGet() < 50) {
            LOGGER.info("Exception for id: 5, counter: {}", counter.get());
            throw new RetryableException("Exception thrown");
        }
        LOGGER.info("Event handled. Event is: {}", event);
    }
}
