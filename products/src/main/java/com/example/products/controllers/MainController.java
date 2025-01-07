package com.example.products.controllers;

import com.example.products.dto.ProductDTO;
import com.example.products.dto.ProductInfoDTO;
import com.example.products.dto.ProductListContainer;
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

    /*
    @KafkaListener(
            topics = "product-request-event-topic",
            containerFactory = "productKafkaListenerContainerFactory"
    )
    public void getProductInfo(Long productId){
        Product product = null;
        try{
            product = productRep.findById(productId).orElseThrow();
        }
        catch (NoSuchElementException ex){
            System.out.println("No such product with id: " + productId); // add logging
        }
        if(product == null) return;
        ProductInfoDTO productInfoDTO = new ProductInfoDTO(product);
        CompletableFuture<SendResult<String, ProductInfoDTO>> future = kafkaTemplateForProduct.send(topicForProduct.name(), productInfoDTO);
        future.whenComplete((res, ex)->{
            if(ex == null) System.out.println("Message sent");
            else System.out.println("Ошибка: " + ex.getMessage());
        });
    }
     */

    @KafkaListener(
            topics = "category-event-topic",
            containerFactory = "categoryKafkaListenerContainerFactory"
    )
    @SendTo
    public ProductListContainer getProductsWithCategory(String categoryTitle) {
        /*
        Category category = null;
        try {
            category = categoryRep.findByTitle(categoryTitle).orElseThrow();
        } catch (NoSuchElementException e) {
            System.out.println("No such category: " + categoryTitle); // add logging
        }
        var result = new ArrayList<ProductDTO>();
        if(category == null) return result;
        category.getProducts().forEach(product -> result.add(new ProductDTO(product)));

         */
        System.out.println(categoryTitle);
        var result = new ArrayList<ProductDTO>();
        result.add(new ProductDTO(1, "POCO", "Desc", "Category", 200));
        result.add(new ProductDTO(2, "POCO", "Desc", "Category", 200));
        return new ProductListContainer(result);
    }

    @KafkaListener(
            topics = "product-request-event-topic",
            containerFactory = "productKafkaListenerContainerFactory"
    )
    @SendTo
    public ProductDTO getProduct(Long productId){
        /*Product product = null;
        try{
            product = productRep.findById(productId).orElseThrow();
        }
        catch (NoSuchElementException e) {
            System.out.println("No such product with id: " + productId); // add logging
        }
        if(product == null) return new ProductDTO();
        else return new ProductDTO(product);*/
        System.out.println("Id: " + productId);
        return new ProductDTO(1, "POCO", "Desc", "Category", 200);
    }
}
