package com.example.gateway.controllers;

import com.example.gateway.dto.ProductDTO;
import com.example.gateway.dto.ProductListContainer;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

@Controller
public class MainController {
    @Autowired
    private ReplyingKafkaTemplate<String, String, ProductListContainer> templateForProducts;
    @Autowired
    private ReplyingKafkaTemplate<String, Long, ProductDTO> templateForProduct;
    @Autowired
    @Qualifier("category")
    private NewTopic categoryTopic;
    @Autowired
    @Qualifier("product")
    private NewTopic productTopic;

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
}
