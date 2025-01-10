package com.example.gateway.controllers;

import com.example.gateway.dto.*;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@Controller
public class MainController {
    @Autowired
    private ReplyingKafkaTemplate<String, String, ProductListContainer> templateForProducts;
    @Autowired
    private ReplyingKafkaTemplate<String, Long, ProductDTO> templateForProduct;
    @Autowired
    private KafkaTemplate<String, ProductInCartDTO> templateForCart;
    @Autowired
    private ReplyingKafkaTemplate<String, ProductAvailabilityDTO, Boolean> templateForChecking;
    @Autowired
    private ReplyingKafkaTemplate<String, UUID, ProductInCartListContainer> templateForGettingProductsFromCart;
    @Autowired
    @Qualifier("category")
    private NewTopic categoryTopic;
    @Autowired
    @Qualifier("product")
    private NewTopic productTopic;
    @Autowired
    @Qualifier("addToCart")
    private NewTopic addToCartTopic;
    @Autowired
    @Qualifier("deleteFromCart")
    private NewTopic deleteFromCartTopic;
    @Autowired
    @Qualifier("checkAvailability")
    private NewTopic checkAvailabilityTopic;
    @Autowired
    @Qualifier("gettingProductsFromCartRequest")
    private NewTopic gettingProductsFromCartTopic;

    @GetMapping("/products/{category}")
    @ResponseBody
    public ArrayList<ProductDTO> getProducts(@PathVariable String category){
        var record = new ProducerRecord<String, String>(categoryTopic.name(), category);
        RequestReplyFuture<String, String, ProductListContainer> future = templateForProducts.sendAndReceive(record);
        ProductListContainer result = new ProductListContainer(new ArrayList<>());
        try {
            result = future.get().value();
        } catch (InterruptedException | ExecutionException e) {
            System.out.println(e.getMessage());
        }
        return result.getList();
    }

    @GetMapping("/product/{productId}")
    @ResponseBody
    public ProductDTO getProduct(@PathVariable long productId){
        var record = new ProducerRecord<String, Long>(productTopic.name(), productId);
        RequestReplyFuture<String, Long, ProductDTO> future = templateForProduct.sendAndReceive(record);
        ProductDTO result =  new ProductDTO();
        try {
            result = future.get().value();
        } catch (InterruptedException | ExecutionException e) {
            System.out.println(e.getMessage());
        }
        return result;
    }

    @PostMapping("/cart/add")
    public ResponseEntity<?> addToCart(@RequestBody ProductDTO productDTO){
        UUID userId = UUID.randomUUID();

        //Implement authorization here

        var record = new ProducerRecord<String, Long>(productTopic.name(), productDTO.getId());
        RequestReplyFuture<String, Long, ProductDTO> future = templateForProduct.sendAndReceive(record);
        ProductDTO result =  new ProductDTO();
        try {
            result = future.get().value();
        } catch (InterruptedException | ExecutionException e) {
            System.out.println(e.getMessage());
        }
        if(result == null) return ResponseEntity.ok("unknown product");
        if(result.getNumber() < productDTO.getNumber()) return ResponseEntity.ok("the product is out of stock");
        templateForCart.send(addToCartTopic.name(), new ProductInCartDTO(userId, productDTO.getId(), productDTO.getNumber()));
        return ResponseEntity.ok("ok");
    }

    @PostMapping("/cart/delete")
    public ResponseEntity<?> deleteFromCart(@RequestBody ProductDTO productDTO){
        UUID userId = UUID.randomUUID();

        //Implement authorization here

        templateForCart.send(deleteFromCartTopic.name(), new ProductInCartDTO(userId, productDTO.getId(), 0));
        return ResponseEntity.ok().build();
    }

    @GetMapping("/cart/availability/{productId}")
    public Boolean checkAvailability(@PathVariable long productId){
        UUID userId = UUID.randomUUID();

        //Implement authorization here

        ProductAvailabilityDTO product = new ProductAvailabilityDTO(userId, productId);
        var record = new ProducerRecord<String, ProductAvailabilityDTO>(checkAvailabilityTopic.name(), product);
        RequestReplyFuture<String, ProductAvailabilityDTO, Boolean> future = templateForChecking.sendAndReceive(record);
        Boolean result = null;
        try {
            result = future.get().value();
        } catch (InterruptedException | ExecutionException e) {
            System.out.println(e.getMessage());
        }
        return result;
    }

    @GetMapping("/cart/products")
    public ArrayList<ProductDTOForCart> getProductsFromCart(){
        UUID userId = UUID.randomUUID();

        //Implement authorization here

        var record = new ProducerRecord<String, UUID>(gettingProductsFromCartTopic.name(), userId);
        RequestReplyFuture<String, UUID, ProductInCartListContainer> future = templateForGettingProductsFromCart.sendAndReceive(record);
        ProductInCartListContainer result = null;
        try {
            result = future.get().value();
        } catch (InterruptedException | ExecutionException e) {
            System.out.println(e.getMessage());
        }
        if(result == null) return new ArrayList<>();

        var response = new ArrayList<ProductDTOForCart>(); // Improve product request
        for(ProductInCartDTO elem: result.getList()){
            var record2 = new ProducerRecord<String, Long>(productTopic.name(), elem.getProductId());
            RequestReplyFuture<String, Long, ProductDTO> future2 = templateForProduct.sendAndReceive(record2);
            ProductDTO result2 =  new ProductDTO();
            try {
                result2 = future2.get().value();
            } catch (InterruptedException | ExecutionException e) {
                System.out.println(e.getMessage());
            }
            if(result2 == null) continue;
            response.add(new ProductDTOForCart(result2, elem));
        }
        return response;
    }
}
