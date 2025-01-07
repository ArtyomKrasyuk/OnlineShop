package com.example.products.dto;

import com.example.products.models.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
    private long id;
    private String title;
    private String description;
    private String category;
    private double price;

    public ProductDTO(Product product){
        this.id = product.getId();
        this.title = product.getTitle();
        this.description = product.getDescription();
        this.category = product.getCategory().getTitle();
        this.price = product.getPrice();
    }
}
