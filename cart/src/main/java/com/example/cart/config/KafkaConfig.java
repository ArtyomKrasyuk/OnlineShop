package com.example.cart.config;

import com.example.cart.dto.ProductAvailabilityDTO;
import com.example.cart.dto.ProductInCartDTO;
import com.example.cart.dto.ProductInCartListContainer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.BooleanSerializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Configuration
@EnableKafka
public class KafkaConfig {
    @Value(value = "${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;

    @Bean
    public ConsumerFactory<String, ProductInCartDTO> consumerFactoryForProductInCart(){
        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "consumer");
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        var serializer = new JsonDeserializer<>(ProductInCartDTO.class);
        serializer.addTrustedPackages("*");
        serializer.setUseTypeHeaders(false);
        return new DefaultKafkaConsumerFactory<>(config, new StringDeserializer(), serializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ProductInCartDTO> containerFactoryForProductInCart(
            ConsumerFactory<String, ProductInCartDTO> consumerFactory
    ) {
        ConcurrentKafkaListenerContainerFactory<String, ProductInCartDTO> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        return factory;
    }

    @Bean
    public ConsumerFactory<String, ProductAvailabilityDTO> consumerFactoryForChecking(){
        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "consumer");
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        var serializer = new JsonDeserializer<>(ProductAvailabilityDTO.class);
        serializer.addTrustedPackages("*");
        serializer.setUseTypeHeaders(false);
        return new DefaultKafkaConsumerFactory<>(config, new StringDeserializer(), serializer);
    }

    @Bean
    public ProducerFactory<String, Boolean> producerFactoryForChecking(){
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, BooleanSerializer.class);
        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ProductAvailabilityDTO> kafkaListenerContainerFactoryForChecking(
            ConsumerFactory<String, ProductAvailabilityDTO> consumerFactory,
            KafkaTemplate<String, Boolean> kafkaTemplate
    ) {
        ConcurrentKafkaListenerContainerFactory<String, ProductAvailabilityDTO> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.setReplyTemplate(kafkaTemplate);
        return factory;
    }

    @Bean
    public KafkaTemplate<String, Boolean> kafkaTemplateForChecking(ProducerFactory<String, Boolean> producerFactory){
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    public ConsumerFactory<String, UUID> consumerFactoryForProducts(){
        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "consumer");
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        var serializer = new JsonDeserializer<>(UUID.class);
        serializer.addTrustedPackages("*");
        serializer.setUseTypeHeaders(false);
        return new DefaultKafkaConsumerFactory<>(config, new StringDeserializer(), serializer);
    }

    @Bean
    public ProducerFactory<String, ProductInCartListContainer> producerFactoryForProducts(){
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, UUID> kafkaListenerContainerFactoryForProducts(
            ConsumerFactory<String, UUID> consumerFactory,
            KafkaTemplate<String, ProductInCartListContainer> kafkaTemplate
    ) {
        ConcurrentKafkaListenerContainerFactory<String, UUID> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.setReplyTemplate(kafkaTemplate);
        return factory;
    }

    @Bean
    public KafkaTemplate<String, ProductInCartListContainer> kafkaTemplateForProducts(ProducerFactory<String, ProductInCartListContainer> producerFactory){
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    public ConsumerFactory<String, ProductInCartListContainer> consumerFactoryForBuyingProducts(){
        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "consumer");
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        var serializer = new JsonDeserializer<>(ProductInCartListContainer.class);
        serializer.addTrustedPackages("*");
        serializer.setUseTypeHeaders(false);
        return new DefaultKafkaConsumerFactory<>(config, new StringDeserializer(), serializer);
    }

    @Bean
    public ProducerFactory<String, Boolean> producerFactoryForBuyingProducts(){
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, BooleanSerializer.class);
        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ProductInCartListContainer> kafkaListenerContainerFactoryForBuyingProducts(
            ConsumerFactory<String, ProductInCartListContainer> consumerFactory,
            KafkaTemplate<String, Boolean> kafkaTemplate
    ) {
        ConcurrentKafkaListenerContainerFactory<String, ProductInCartListContainer> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.setReplyTemplate(kafkaTemplate);
        return factory;
    }

    @Bean
    public KafkaTemplate<String, Boolean> kafkaTemplateForBuyingProducts(ProducerFactory<String, Boolean> producerFactory){
        return new KafkaTemplate<>(producerFactory);
    }
}
