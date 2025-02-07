package com.example.products.controllers;

import com.example.products.dto.*;
import com.example.products.models.Category;
import com.example.products.models.Product;
import com.example.products.repos.*;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.CompletableFuture;

@RestController
public class MainController {
    @Autowired
    private ProductRepository productRep;
    @Autowired
    private FilterRepository filterRep;
    @Autowired
    private CategoryRepository categoryRep;
    @Autowired
    private CharacteristicRepository characteristicRep;
    @Autowired
    private CharacteristicsRepository characteristicsRep;

    @KafkaListener(
            topics = "category-event-topic",
            containerFactory = "categoryKafkaListenerContainerFactory"
    )
    @SendTo
    public ProductListContainer getProductsWithCategory(String categoryTitle) {
        Category category = null;
        try {
            category = categoryRep.findByTitle(categoryTitle).orElseThrow();
        } catch (NoSuchElementException e) {
            System.out.println("No such category: " + categoryTitle); // add logging
        }
        var result = new ProductListContainer();
        if(category == null) return result;
        category.getProducts().forEach(product -> result.getList().add(new ProductDTO(product)));
        return result;
    }

    @KafkaListener(
            topics = "product-request-event-topic",
            containerFactory = "productKafkaListenerContainerFactory"
    )
    @SendTo
    public ProductDTO getProduct(Long productId){
        Product product = null;
        try{
            product = productRep.findById(productId).orElseThrow();
        }
        catch (NoSuchElementException e) {
            System.out.println("No such product with id: " + productId); // add logging
        }
        if(product == null) return new ProductDTO();
        else return new ProductDTO(product);
    }

    @KafkaListener(
            topics = "getting-products-by-id-event-topic",
            containerFactory = "kafkaListenerContainerFactoryForProductsBuId"
    )
    @SendTo
    public ProductListContainer getProductsById(ProductIdListContainer container) {
        ArrayList<ProductDTO> list = new ArrayList<>();
        for(Long productId: container.getList()){
            Product product = null;
            try{
                product = productRep.findById(productId).orElseThrow();
            }
            catch (NoSuchElementException ex){
                System.out.println(ex.getMessage());
            }
            if(product != null) list.add(new ProductDTO(product));
        }
        return new ProductListContainer(list);
    }

    @KafkaListener(
            topics = "change-number-of-products-request-event-topic",
            containerFactory = "kafkaListenerContainerFactoryForChangingProducts"
    )
    @SendTo
    public String changeOfProducts(ProductChangeListContainer container) {
        var list = new ArrayList<Product>();
        for(ProductChangeDTO elem: container.getList()){
            try{
                Product product = productRep.findById(elem.getProductId()).orElseThrow();
                if(container.isIncrease()) product.setNumber(product.getNumber() + elem.getNumber());
                else{
                    if(product.getNumber() < elem.getNumber()) return "Big number";
                    product.setNumber(product.getNumber() - elem.getNumber());
                }
                list.add(product);
            }
            catch (NoSuchElementException ex){
                System.out.println(ex.getMessage());
                return "Unknown product";
            }
        }
        productRep.saveAll(list); //Do exception handling
        return "ok";
    }
}
