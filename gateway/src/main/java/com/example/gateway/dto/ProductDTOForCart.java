package com.example.gateway.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTOForCart {
    private long id;
    private String title;
    private String description;
    private String category;
    private double price;
    private int number;
    private int numberInCart;

    public ProductDTOForCart(ProductDTO productDTO, ProductInCartDTO productInCartDTO){
        this.id = productDTO.getId();
        this.title = productDTO.getTitle();
        this.description = productDTO.getDescription();
        this.category = productDTO.getCategory();
        this.price = productDTO.getPrice();
        this.number = productDTO.getNumber();
        this.numberInCart = productInCartDTO.getNumber();
    }
}
