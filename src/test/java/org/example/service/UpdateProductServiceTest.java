package org.example.service;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.example.models.event.UpdateProductEvent;
import org.example.utils.BaseTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.test.utils.KafkaTestUtils;

import java.time.Duration;
import java.util.List;
import java.util.Map;


public class UpdateProductServiceTest extends BaseTest {

    @Autowired
    private UpdateProductService service;

    @Test
    public void testUpdateProduct() {
        UpdateProductEvent event = new UpdateProductEvent("333", "New Product Name");
        service.updateProduct(event, false);
        try {
            service.updateProduct(new UpdateProductEvent("555", "New Product Name Exception 5"), true);
        } catch (Exception e) {
            System.out.println("Exception was thrown");
        }
        try {
            service.updateProduct(new UpdateProductEvent("777", "New Product Name Exception"), true);
        } catch (Exception e) {
            System.out.println("Exception was thrown");
        }
        service.updateProduct(new UpdateProductEvent("237", "New Product Name 237"), false);

        var consumerFactory = new DefaultKafkaConsumerFactory<String, UpdateProductEvent>(getConsumerProperties());
        Consumer<String, UpdateProductEvent> testConsumer = consumerFactory.createConsumer("test-1", "test-1");
        testConsumer.subscribe(List.of("update-product-events-topic"));

        ConsumerRecords<String, UpdateProductEvent> consumerRecords = KafkaTestUtils.getRecords(
                testConsumer,
                Duration.ofSeconds(5),
                1
        );
        for (ConsumerRecord<String, UpdateProductEvent> record : consumerRecords) {
            System.out.println(record.value());
        }

        var consumerFactorySecond = new DefaultKafkaConsumerFactory<String, UpdateProductEvent>(getConsumerPropertiesReadCommited());
        Consumer<String, UpdateProductEvent> testConsumerSecond = consumerFactorySecond.createConsumer("test-2", "test-2");
        testConsumerSecond.subscribe(List.of("update-product-events-topic"));

        ConsumerRecords<String, UpdateProductEvent> consumerRecordsSecond = KafkaTestUtils.getRecords(
                testConsumerSecond,
                Duration.ofSeconds(5),
                1
        );
        for (ConsumerRecord<String, UpdateProductEvent> record : consumerRecordsSecond) {
            System.out.println(record.value());
        }
    }

    private Map<String, Object> getConsumerPropertiesReadCommited() {
        return Map.of(
                "bootstrap.servers", bootstrapAddress,
                "auto.offset.reset", "earliest",
                "key.deserializer", StringDeserializer.class,
                "value.deserializer", JsonDeserializer.class,
                JsonDeserializer.TRUSTED_PACKAGES, "*",
                "isolation.level", "read_committed"
        );
    }
}
