package com.example.gateway.config;

import com.example.gateway.dto.*;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.BooleanDeserializer;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.common.utils.Java;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.*;

@Configuration
@EnableKafka
public class KafkaConfig {
    @Value(value = "${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;

    @Bean
    public KafkaAdmin kafkaAdmin(){
        Map<String, Object> config = new HashMap<>();
        config.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        return new KafkaAdmin(config);
    }

    @Bean("category")
    public NewTopic createTopic(){
        return new NewTopic("category-event-topic", 1, (short) 1);
    }

    @Bean("product")
    public NewTopic createProductTopic(){
        return new NewTopic("product-request-event-topic", 1, (short) 1);
    }

    @Bean("responseProducts")
    public NewTopic createTopicForProducts(){
        return new NewTopic("list-of-products-event-topic", 1, (short) 1);
    }

    @Bean("responseProduct")
    public NewTopic createTopicForProduct(){
        return new NewTopic("product-response-event-topic", 1, (short) 1);
    }

    @Bean("addToCart")
    public NewTopic addProductToCart(){
        return new NewTopic("add-to-cart-event-topic", 1, (short) 1);
    }

    @Bean("deleteFromCart")
    public NewTopic deleteFromCart(){
        return new NewTopic("delete-from-cart-event-topic", 1, (short) 1);
    }

    @Bean("checkAvailability")
    public NewTopic checkAvailabilityTopic(){
        return new NewTopic("check-availability-event-topic", 1, (short) 1);
    }

    @Bean("checkAvailabilityResponse")
    public NewTopic checkAvailabilityResponseTopic(){
        return new NewTopic("check-availability-response-event-topic", 1, (short) 1);
    }

    @Bean("gettingProductsFromCartRequest")
    public NewTopic gettingProductsFromCartRequestTopic(){
        return new NewTopic("getting-products-from-cart-event-topic", 1, (short) 1);
    }

    @Bean("gettingProductsFromCartResponse")
    public NewTopic gettingProductsFromCartResponseTopic(){
        return new NewTopic("getting-products-from-cart-response-event-topic", 1, (short) 1);
    }

    /*@Bean
    public ConcurrentKafkaListenerContainerFactory<String, ProductListContainer> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, ProductListContainer> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }*/

    /*@Bean
    public KafkaTemplate<String, String> kafkaTemplate(){
        return new KafkaTemplate<>(producerFactory());
    }*/

    // ---------------------------------List of products------------------------------------------

    @Bean
    public ConsumerFactory<String, ProductListContainer> consumerFactoryForProducts(){
        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "consumer");
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        var serializer = new JsonDeserializer<>(ProductListContainer.class);
        serializer.addTrustedPackages("*");
        serializer.setUseTypeHeaders(false);
        return new DefaultKafkaConsumerFactory<>(config, new StringDeserializer(), serializer);
    }

    @Bean
    public ProducerFactory<String, String> producerFactoryForProducts(){
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    public KafkaMessageListenerContainer<String, ProductListContainer> replyContainerForProducts(
            @Qualifier("responseProducts") NewTopic topic,
            ConsumerFactory<String, ProductListContainer> consumerFactory
    ) {
        ContainerProperties containerProperties = new ContainerProperties(topic.name());
        return new KafkaMessageListenerContainer<>(consumerFactory, containerProperties);
    }

    @Bean
    public ReplyingKafkaTemplate<String, String, ProductListContainer> replyingKafkaTemplateForProducts(
            ProducerFactory<String, String> producerFactory,
            KafkaMessageListenerContainer<String, ProductListContainer> replyContainer
            ) {
        return new ReplyingKafkaTemplate<>(producerFactory, replyContainer);
    }

    // ---------------------------------One product----------------------------------------------

    @Bean
    public ConsumerFactory<String, ProductDTO> consumerFactoryForProduct(){
        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "consumer");
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        var serializer = new JsonDeserializer<>(ProductDTO.class);
        serializer.addTrustedPackages("*");
        serializer.setUseTypeHeaders(false);
        return new DefaultKafkaConsumerFactory<>(config, new StringDeserializer(), serializer);
    }

    @Bean
    public ProducerFactory<String, Long> producerFactoryForProduct(){
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, LongSerializer.class);
        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    public KafkaMessageListenerContainer<String, ProductDTO> replyContainerForProduct(
            @Qualifier("responseProduct") NewTopic topic,
            ConsumerFactory<String, ProductDTO> consumerFactory
    ) {
        ContainerProperties containerProperties = new ContainerProperties(topic.name());
        return new KafkaMessageListenerContainer<>(consumerFactory, containerProperties);
    }

    @Bean
    public ReplyingKafkaTemplate<String, Long, ProductDTO> replyingKafkaTemplateForProduct(
            ProducerFactory<String, Long> producerFactory,
            KafkaMessageListenerContainer<String, ProductDTO> replyContainer
    ) {
        return new ReplyingKafkaTemplate<>(producerFactory, replyContainer);
    }

    // ---------------------------------Product in cart-----------------------------------------

    @Bean
    public ProducerFactory<String, ProductInCartDTO> producerFactoryForAddingToCart(){
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    public KafkaTemplate<String, ProductInCartDTO> kafkaTemplateForCart(ProducerFactory<String, ProductInCartDTO> factory) {
        return new KafkaTemplate<>(factory);
    }

    @Bean
    public ConsumerFactory<String, Boolean> consumerFactoryForProductInChecking(){
        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "consumer");
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, BooleanDeserializer.class);
        return new DefaultKafkaConsumerFactory<>(config);
    }

    @Bean
    public ProducerFactory<String, ProductAvailabilityDTO> producerFactoryForProductChecking(){
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    public KafkaMessageListenerContainer<String, Boolean> replyContainerForProductChecking(
            @Qualifier("checkAvailabilityResponse") NewTopic topic,
            ConsumerFactory<String, Boolean> consumerFactory
    ) {
        ContainerProperties containerProperties = new ContainerProperties(topic.name());
        return new KafkaMessageListenerContainer<>(consumerFactory, containerProperties);
    }

    @Bean
    public ReplyingKafkaTemplate<String, ProductAvailabilityDTO, Boolean> replyingKafkaTemplateForChecking(
            ProducerFactory<String, ProductAvailabilityDTO> producerFactory,
            KafkaMessageListenerContainer<String, Boolean> replyContainer
    ) {
        return new ReplyingKafkaTemplate<>(producerFactory, replyContainer);
    }

    @Bean
    public ConsumerFactory<String, ProductInCartListContainer> consumerFactoryForGettingProductsFromCart(){
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
    public ProducerFactory<String, UUID> producerFactoryForGettingProductsFromCart(){
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    public KafkaMessageListenerContainer<String, ProductInCartListContainer> replyContainerForGettingProductsFromCart(
            @Qualifier("gettingProductsFromCartResponse") NewTopic topic,
            ConsumerFactory<String, ProductInCartListContainer> consumerFactory
    ) {
        ContainerProperties containerProperties = new ContainerProperties(topic.name());
        return new KafkaMessageListenerContainer<>(consumerFactory, containerProperties);
    }

    @Bean
    public ReplyingKafkaTemplate<String, UUID, ProductInCartListContainer> replyingKafkaTemplateForGettingProductsFromCart(
            ProducerFactory<String, UUID> producerFactory,
            KafkaMessageListenerContainer<String, ProductInCartListContainer> replyContainer
    ) {
        return new ReplyingKafkaTemplate<>(producerFactory, replyContainer);
    }
}
