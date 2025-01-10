package com.example.cart.controllers;

import com.example.cart.dto.ProductAvailabilityDTO;
import com.example.cart.dto.ProductInCartDTO;
import com.example.cart.dto.ProductInCartListContainer;
import com.example.cart.models.Product;
import com.example.cart.models.EmbeddedId;
import com.example.cart.repos.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Controller
public class MainController {
    @Autowired
    private ProductRepository repository;

    @KafkaListener(
            topics = "add-to-cart-event-topic",
            containerFactory = "containerFactoryForProductInCart"
    )
    public void addToCart(ProductInCartDTO product){
        EmbeddedId embeddedId = new EmbeddedId(product.getUserId(), product.getProductId());
        repository.save(new Product(embeddedId, product.getNumber()));
    }

    @KafkaListener(
            topics = "delete-from-cart-event-topic",
            containerFactory = "containerFactoryForProductInCart"
    )
    public void deleteFromCart(ProductInCartDTO product){
        EmbeddedId embeddedId = new EmbeddedId(product.getUserId(), product.getProductId());
        repository.deleteById(embeddedId);
    }

    @KafkaListener(
            topics = "check-availability-event-topic",
            containerFactory = "kafkaListenerContainerFactoryForChecking"
    )
    @SendTo
    public Boolean checkProduct(ProductAvailabilityDTO product){
        EmbeddedId id = new EmbeddedId(product.getUserId(), product.getProductId());
        return repository.existsById(id);
    }

    @KafkaListener(
            topics = "getting-products-from-cart-event-topic",
            containerFactory = "kafkaListenerContainerFactoryForProducts"
    )
    @SendTo
    public ProductInCartListContainer checkProduct(UUID userId){
        List<Product> result = new ArrayList<>();
        try{
            result = repository.findByUserId(userId).orElseThrow();
        }
        catch (NoSuchElementException ex){
            System.out.println(ex);
        }
        ArrayList<ProductInCartDTO> responseBody = new ArrayList<>();
        result.forEach(elem -> responseBody.add(new ProductInCartDTO(elem)));
        return new ProductInCartListContainer(responseBody);
    }
}
