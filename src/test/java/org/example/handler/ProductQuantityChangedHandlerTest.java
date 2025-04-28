package org.example.handler;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.example.models.event.ProductQuantityChangedEvent;
import org.example.models.event.WrongEvent;
import org.example.utils.BaseTest;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.utils.KafkaTestUtils;

import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ProductQuantityChangedHandlerTest extends BaseTest {

    @Test
    public void getEvent() throws InterruptedException {
        var factory = new DefaultKafkaProducerFactory<>(getProducerProperties());
        var kafkaTemplate = new KafkaTemplate<>(factory);

        ProductQuantityChangedEvent event = new ProductQuantityChangedEvent("Some ID", 5);
        kafkaTemplate.send("product-quantity-changed-events-topic", event);

//        var consumer = new DefaultKafkaConsumerFactory<String, ProductQuantityChangedEvent>(getConsumerProperties());
//        ConsumerRecord<String, ProductQuantityChangedEvent> consumerRecord = KafkaTestUtils.getSingleRecord(
//                consumer.createConsumer(),
//                "product-quantity-changed-events-topic",
//                Duration.ofMillis(10000)
//        );
//
//        ProductQuantityChangedEvent value = consumerRecord.value();
//        assertThat(event).isNotNull();
//        assertThat(value).isEqualTo(event);

        Thread.sleep(1000);
    }

    @Test
    public void testWrongEvent() {
        var factory = new DefaultKafkaProducerFactory<>(getProducerProperties());
        var kafkaTemplate = new KafkaTemplate<>(factory);

        WrongEvent event = new WrongEvent("Wrong event", "Try to get error with Deserialization");
        kafkaTemplate.send("product-quantity-changed-events-topic", event);

        var consumerFactory = new DefaultKafkaConsumerFactory<String, WrongEvent>(getConsumerProperties());
        Consumer<String, WrongEvent> testConsumer = consumerFactory.createConsumer("test-group", "test");
        testConsumer.subscribe(List.of("product-quantity-changed-events-topic.DTL"));

        ConsumerRecord<String, WrongEvent> consumerRecord = KafkaTestUtils.getSingleRecord(
                testConsumer,
                "product-quantity-changed-events-topic.DTL",
                Duration.ofMillis(10000)
        );

        WrongEvent value = consumerRecord.value();
        assertThat(event).isNotNull();
        assertThat(value).isEqualTo(event);
    }
}
