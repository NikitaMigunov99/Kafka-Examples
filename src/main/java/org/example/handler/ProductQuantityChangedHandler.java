package org.example.handler;

import org.example.models.event.ProductQuantityChangedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ProductQuantityChangedHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductQuantityChangedHandler.class);

    @KafkaListener(topics = "product-quantity-changed-events-topic")
    public void handleEvent(ProductQuantityChangedEvent event) {
        LOGGER.info("Event handled. Event is: {}", event.toString());
    }
}
