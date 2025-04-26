package org.example.handler;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.example.models.event.ProductQuantityChangedEvent;
import org.example.utils.BaseTest;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.utils.KafkaTestUtils;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

public class ProductQuantityChangedHandlerTest extends BaseTest {

    @Test
    public void getEvent() {
        var factory = new DefaultKafkaProducerFactory<>(getProducerProperties());
        var kafkaTemplate = new KafkaTemplate<>(factory);

        ProductQuantityChangedEvent event = new ProductQuantityChangedEvent("Some ID", 5);
        kafkaTemplate.send("product-quantity-changed-events-topic", event);

        var consumer = new DefaultKafkaConsumerFactory<String, ProductQuantityChangedEvent>(getConsumerProperties());
        ConsumerRecord<String, ProductQuantityChangedEvent> consumerRecord = KafkaTestUtils.getSingleRecord(
                consumer.createConsumer(),
                "product-quantity-changed-events-topic",
                Duration.ofMillis(10000)
        );

        ProductQuantityChangedEvent value = consumerRecord.value();
        assertThat(event).isNotNull();
        assertThat(value).isEqualTo(event);
    }
}
