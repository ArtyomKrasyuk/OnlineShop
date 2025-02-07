package com.example.products.config;

import com.example.products.dto.ProductChangeListContainer;
import com.example.products.dto.ProductIdListContainer;
import com.example.products.dto.ProductInfoDTO;
import com.example.products.dto.ProductListContainer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {
    @Value(value = "${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;

    //---------------------------------------Category-----------------------------------------------------

    @Bean
    public ConsumerFactory<String, String> consumerFactoryForCategory(){
        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "consumer");
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        return new DefaultKafkaConsumerFactory<>(config);
    }

    @Bean
    public ProducerFactory<String, ProductListContainer> producerFactoryForCategory(){
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> categoryKafkaListenerContainerFactory(
            ConsumerFactory<String, String> consumerFactory,
            KafkaTemplate<String, ProductListContainer> kafkaTemplate
    ) {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.setReplyTemplate(kafkaTemplate);
        return factory;
    }

    @Bean
    public KafkaTemplate<String, ProductListContainer> kafkaTemplateForCategory(ProducerFactory<String, ProductListContainer> producerFactory){
        return new KafkaTemplate<>(producerFactory);
    }

    //---------------------------------------Product------------------------------------------------------

    @Bean
    public ConsumerFactory<String, Long> consumerFactoryForProduct(){
        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "consumer");
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class);
        return new DefaultKafkaConsumerFactory<>(config);
    }

    @Bean
    public ProducerFactory<String, ProductInfoDTO> producerFactoryForProduct(){
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Long> productKafkaListenerContainerFactory(
            ConsumerFactory<String, Long> consumerFactory,
            KafkaTemplate<String, ProductInfoDTO> kafkaTemplate
    ) {
        ConcurrentKafkaListenerContainerFactory<String, Long> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.setReplyTemplate(kafkaTemplate);
        return factory;
    }

    @Bean
    public KafkaTemplate<String, ProductInfoDTO> kafkaTemplateForProduct(ProducerFactory<String, ProductInfoDTO> producerFactory){
        return new KafkaTemplate<>(producerFactory);
    }

    //---------------------------------------Products------------------------------------------------------

    @Bean
    public ConsumerFactory<String, ProductIdListContainer> consumerFactoryForProductsBuId(){
        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "consumer");
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        var serializer = new JsonDeserializer<>(ProductIdListContainer.class);
        serializer.addTrustedPackages("*");
        serializer.setUseTypeHeaders(false);
        return new DefaultKafkaConsumerFactory<>(config, new StringDeserializer(), serializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ProductIdListContainer> kafkaListenerContainerFactoryForProductsBuId(
            ConsumerFactory<String, ProductIdListContainer> consumerFactory,
            KafkaTemplate<String, ProductListContainer> kafkaTemplate
    ) {
        ConcurrentKafkaListenerContainerFactory<String, ProductIdListContainer> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.setReplyTemplate(kafkaTemplate);
        return factory;
    }

    //---------------------------------------Changing of products------------------------------------------------------

    @Bean
    public ConsumerFactory<String, ProductChangeListContainer> consumerFactoryForChangingProducts(){
        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "consumer");
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        var serializer = new JsonDeserializer<>(ProductChangeListContainer.class);
        serializer.addTrustedPackages("*");
        serializer.setUseTypeHeaders(false);
        return new DefaultKafkaConsumerFactory<>(config, new StringDeserializer(), serializer);
    }

    @Bean
    public ProducerFactory<String, String> producerFactoryForChangingProducts(){
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ProductChangeListContainer> kafkaListenerContainerFactoryForChangingProducts(
            ConsumerFactory<String, ProductChangeListContainer> consumerFactory,
            KafkaTemplate<String, String> kafkaTemplate
    ) {
        ConcurrentKafkaListenerContainerFactory<String, ProductChangeListContainer> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.setReplyTemplate(kafkaTemplate);
        return factory;
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplateForChangingProducts(ProducerFactory<String, String> producerFactory){
        return new KafkaTemplate<>(producerFactory);
    }
}
