package org.example.handler;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.example.models.event.ProductQuantityChangedEvent;
import org.example.models.event.WrongEvent;
import org.example.utils.BaseTest;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.utils.KafkaTestUtils;

import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProductQuantityChangedHandlerTest extends BaseTest {

    @Test
    @Order(1)
    public void getEvent() {
        var factory = new DefaultKafkaProducerFactory<>(getProducerProperties());
        var kafkaTemplate = new KafkaTemplate<>(factory);

        ProductQuantityChangedEvent event = new ProductQuantityChangedEvent("Some ID", 5);
        kafkaTemplate.send("product-quantity-changed-events-topic", event);

        var consumerFactory = new DefaultKafkaConsumerFactory<String, ProductQuantityChangedEvent>(getConsumerProperties());
        Consumer<String, ProductQuantityChangedEvent> testConsumer = consumerFactory.createConsumer("test-group-1", "test");
        testConsumer.subscribe(List.of("product-quantity-changed-events-topic"));

        ConsumerRecord<String, ProductQuantityChangedEvent> consumerRecord = KafkaTestUtils.getSingleRecord(
                testConsumer,
                "product-quantity-changed-events-topic",
                Duration.ofMillis(10000)
        );

        ProductQuantityChangedEvent value = consumerRecord.value();
        assertThat(value).isNotNull();
        assertThat(value).isEqualTo(event);
    }

    @Test
    @Order(2)
    public void testWrongEvent() {
        var factory = new DefaultKafkaProducerFactory<>(getProducerProperties());
        var kafkaTemplate = new KafkaTemplate<>(factory);

        ProductQuantityChangedEvent normalEventFirst = new ProductQuantityChangedEvent("New ID first", 255);
        kafkaTemplate.send("product-quantity-changed-events-topic", normalEventFirst);

        WrongEvent event = new WrongEvent("Wrong event", "Try to get error with Deserialization");
        kafkaTemplate.send("product-quantity-changed-events-topic", event);

        ProductQuantityChangedEvent normalEventSecond = new ProductQuantityChangedEvent("New ID second", 333);
        kafkaTemplate.send("product-quantity-changed-events-topic", normalEventFirst);

        var consumerFactory = new DefaultKafkaConsumerFactory<String, WrongEvent>(getConsumerProperties());
        Consumer<String, WrongEvent> testConsumer = consumerFactory.createConsumer("test-group-2", "test");
        testConsumer.subscribe(List.of("product-quantity-changed-events-topic.DLT"));

        ConsumerRecord<String, WrongEvent> consumerRecord = KafkaTestUtils.getSingleRecord(
                testConsumer,
                "product-quantity-changed-events-topic.DLT",
                Duration.ofMillis(10000)
        );

        WrongEvent value = consumerRecord.value();
        assertThat(value).isNotNull();
        assertThat(value).isEqualTo(event);

        ProductQuantityChangedEvent normalEvent = new ProductQuantityChangedEvent("New ID", 155);
        kafkaTemplate.send("product-quantity-changed-events-topic", normalEvent);
    }

    @Test
    @Order(3)
    public void testRetryableException() throws InterruptedException {
        var factory = new DefaultKafkaProducerFactory<>(getProducerProperties());
        var kafkaTemplate = new KafkaTemplate<>(factory);

        for (int i = 0; i < 50; i++) {
            ProductQuantityChangedEvent event = new ProductQuantityChangedEvent(String.valueOf(i), i);
            kafkaTemplate.send("product-quantity-changed-events-topic", event);
        }
        Thread.sleep(10500);
    }

    @Test
    @Order(4)
    public void checkIdempotence() throws InterruptedException {
        var factory = new DefaultKafkaProducerFactory<String, Object>(getProducerProperties());
        var kafkaTemplate = new KafkaTemplate<>(factory);

        String requestIdFirst = "111";
        ProductQuantityChangedEvent firstEvent = new ProductQuantityChangedEvent("11111", 5);
        ProducerRecord<String, Object> firstRecord = new
                ProducerRecord<>(
                "product-quantity-changed-events-topic",
                firstEvent);
        firstRecord.headers().add("requestId", requestIdFirst.getBytes());
        kafkaTemplate.send(firstRecord);

        String requestIdSecond = "222";
        int i = 0;
        while (i < 3) {
            ProductQuantityChangedEvent secondEvent = new ProductQuantityChangedEvent("22222" + i, 7);
            ProducerRecord<String, Object> secondRecord = new
                    ProducerRecord<>(
                    "product-quantity-changed-events-topic",
                    secondEvent);
            secondRecord.headers().add("requestId", requestIdSecond.getBytes());
            kafkaTemplate.send(secondRecord);
            i++;
        }
        Thread.sleep(1000);
    }
}
