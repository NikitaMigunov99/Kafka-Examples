package org.example.handler;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.example.exception.RetryableException;
import org.example.models.event.ProductQuantityChangedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class ProductQuantityChangedHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductQuantityChangedHandler.class);

    private final AtomicInteger counter = new AtomicInteger(0);

    @KafkaListener(topics = "product-quantity-changed-events-topic", groupId = "product-quantity-changed")
    public void handleEvent(ConsumerRecord<String, ProductQuantityChangedEvent> record, Acknowledgment ack) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        String time = now.format(formatter);
        ProductQuantityChangedEvent event = record.value();
        LOGGER.info("Processing event with id: {}, thread: {}, time: {}", event.getProductId(), Thread.currentThread().getId(), time);
        if (event.getProductId().equals("5") && counter.incrementAndGet() < 50) {
            LOGGER.info("Exception for id: 5, counter: {}", counter.get());
            throw new RetryableException("Exception thrown");
        }
        ack.acknowledge();
        LOGGER.info("Event handled. Event is: {}", event);
    }
}
