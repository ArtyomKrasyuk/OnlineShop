package com.example.cart.dto;

import com.example.cart.models.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductInCartDTO {
    private UUID userId;
    private long productId;
    private int number;

    public ProductInCartDTO(Product product){
        this.userId = product.getEmbeddedId().getUserId();
        this.productId = product.getEmbeddedId().getProductId();
        this.number = product.getNumber();
    }
}
