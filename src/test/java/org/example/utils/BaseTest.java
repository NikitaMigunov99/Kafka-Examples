package org.example.utils;

import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.Map;

@SpringBootTest
public abstract class BaseTest {

    private static final KafkaContainer kafkaContainer = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:6.2.1"));

    @Value(value = "${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;

    static {
        kafkaContainer.start();
    }

    @DynamicPropertySource
    static void kafkaProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", kafkaContainer::getBootstrapServers);
        System.out.println("spring.kafka.bootstrap-servers: "+ kafkaContainer.getBootstrapServers());
    }

    protected Map<String, Object> getConsumerProperties() {
        return Map.of(
                "bootstrap.servers", bootstrapAddress,
                "auto.offset.reset", "earliest",
                "key.deserializer", StringDeserializer.class,
                "value.deserializer", JsonDeserializer.class,
                JsonDeserializer.TRUSTED_PACKAGES, "*"
        );
    }

    protected Map<String, Object> getProducerProperties() {
        return Map.of(
                "bootstrap.servers", bootstrapAddress,
                "acks", "all",
                "key.serializer", StringSerializer.class,
                "value.serializer", JsonSerializer.class);
    }
}
