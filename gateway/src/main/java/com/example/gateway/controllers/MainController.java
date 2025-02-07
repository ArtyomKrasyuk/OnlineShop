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
import java.util.Arrays;
import java.util.Comparator;
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
    private ReplyingKafkaTemplate<String, ProductIdListContainer, ProductListContainer> templateForProductsById;
    @Autowired
    private ReplyingKafkaTemplate<String, ProductChangeListContainer, String> templateForChangingProducts;
    @Autowired
    private ReplyingKafkaTemplate<String, ProductInCartListContainer, Boolean> templateForBuyingProducts;
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
    @Autowired
    @Qualifier("gettingProductsById")
    private NewTopic gettingProductsByIdTopic;
    @Autowired
    @Qualifier("changeNumberOfProductsRequest")
    private NewTopic changeNumberOfProductsRequestTopic;
    @Qualifier("buyProductsRequest")
    private NewTopic buyProductsRequestTopic;

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
        ProductIdListContainer container = new ProductIdListContainer();
        result.getList().forEach(elem -> container.getList().add(elem.getProductId()));

        var record2 = new ProducerRecord<String, ProductIdListContainer>(gettingProductsByIdTopic.name(), container);
        RequestReplyFuture<String, ProductIdListContainer, ProductListContainer> future2 = templateForProductsById.sendAndReceive(record2);
        ProductListContainer result2 =  null;
        try {
            result2 = future2.get().value();
        } catch (InterruptedException | ExecutionException e) {
            System.out.println(e.getMessage());
        }
        if(result2 == null || result.getList().size() != result2.getList().size()) return new ArrayList<>();

        var response = new ArrayList<ProductDTOForCart>();
        for(int i = 0; i < result2.getList().size(); i++){
            response.add(new ProductDTOForCart(result2.getList().get(i), result.getList().get(i)));
        }

        return response;
    }

    @PostMapping("/cart/buy")
    @ResponseBody
    public String buyProducts(@RequestBody ArrayList<ProductDTOForCart> products){
        UUID userId = UUID.randomUUID();

        //Implement authorization here

        ArrayList<ProductChangeDTO> data = new ArrayList<>();
        products.forEach(elem -> data.add(new ProductChangeDTO(elem.getId(), elem.getNumberInCart())));
        ProductChangeListContainer container = new ProductChangeListContainer(data, false);

        var record = new ProducerRecord<String, ProductChangeListContainer>(changeNumberOfProductsRequestTopic.name(), container);
        RequestReplyFuture<String, ProductChangeListContainer, String> future = templateForChangingProducts.sendAndReceive(record);
        String result = null;
        try {
            result = future.get().value();
        } catch (InterruptedException | ExecutionException e) {
            System.out.println(e.getMessage());
        }
        if(result == null) return "Error";
        else if(!result.equals("ok")) return result;

        ArrayList<ProductInCartDTO> dataForCart = new ArrayList<>();
        products.forEach(elem -> dataForCart.add(new ProductInCartDTO(userId, elem.getId(), elem.getNumberInCart())));
        ProductInCartListContainer containerForCart = new ProductInCartListContainer(dataForCart);

        var recordForCart = new ProducerRecord<String, ProductInCartListContainer>(buyProductsRequestTopic.name(), containerForCart);
        RequestReplyFuture<String, ProductInCartListContainer, Boolean> futureForCart = templateForBuyingProducts.sendAndReceive(recordForCart);
        boolean resultForCart = false;
        try {
            resultForCart = futureForCart.get().value();
        } catch (InterruptedException | ExecutionException e) {
            System.out.println(e.getMessage());
        }

        if(resultForCart) return "ok";
        else{
            container.setIncrease(true);
            RequestReplyFuture<String, ProductChangeListContainer, String> errorFuture = templateForChangingProducts.sendAndReceive(record);
            String errorResult = null;
            try {
                errorResult = errorFuture.get().value();
            } catch (InterruptedException | ExecutionException e) {
                System.out.println(e.getMessage());
            }
            if(errorResult != null && !errorResult.equals("ok")) {
                System.out.println("Error in changing number of products");
                products.forEach(elem -> System.out.println("ID: " + elem.getId() + " Number: " + elem.getNumberInCart()));
            }
            return "Payment error";
        }
    }
}
