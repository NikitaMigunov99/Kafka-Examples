package org.example.service;

import lombok.extern.slf4j.Slf4j;
import org.example.models.event.AuditEvent;
import org.example.models.event.UpdateProductEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Service
@Slf4j
public class UpdateProductService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public UpdateProductService(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Transactional(transactionManager = "kafkaTransactionManager")
    public void updateProduct(UpdateProductEvent event, boolean throwException) {
        log.info("New event {}", event);
        boolean isWithinTransaction = TransactionSynchronizationManager.isActualTransactionActive();
        log.info("First is transaction available {}", isWithinTransaction);
        kafkaTemplate.send("update-product-events-topic", event);

        log.info("Second is transaction available {}", isWithinTransaction);

        AuditEvent auditEvent = new AuditEvent(event.getProductId(), "update-product");
        kafkaTemplate.send("audit-events-topic", auditEvent);

        log.info("Third is transaction available {}", isWithinTransaction);
        if (throwException) {
            try {
                Thread.sleep(1000);
                throw new RuntimeException("Something went wrong");
            } catch (InterruptedException e) {
                log.error("Unknown error ", e);
            }
        }
    }
}
