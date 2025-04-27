package org.example.service;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.example.models.CreateProductDTO;
import org.example.models.event.CreateProductEvent;
import org.example.utils.BaseTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.test.utils.KafkaTestUtils;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class CreateProductServiceTest extends BaseTest {

    private static final String TOPIC = "product-created-events-topic";

    @Autowired
    private CreateProductService service;

    @Test
    public void testSendAndReceiveMessage() {
        service.createProduct(new CreateProductDTO("Computer", new BigDecimal("100.00"), 5));

        var consumerFactory = new DefaultKafkaConsumerFactory<String, CreateProductEvent>(getConsumerProperties());
        Consumer<String, CreateProductEvent> testConsumer = consumerFactory.createConsumer("test", "test");
        testConsumer.subscribe(List.of(TOPIC));

        ConsumerRecord<String, CreateProductEvent> consumerRecord = KafkaTestUtils.getSingleRecord(
                testConsumer,
                TOPIC,
                Duration.ofMillis(10000)
        );

        CreateProductEvent event = consumerRecord.value();
        assertThat(event).isNotNull();
        assertThat(event.getTitle()).isEqualTo(new BigDecimal("Computer"));
        assertThat(event.getPrice()).isEqualTo(new BigDecimal("100.00"));
        assertThat(event.getQuantity()).isEqualTo(5);
    }
}
