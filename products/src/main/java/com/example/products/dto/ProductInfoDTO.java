package com.example.products.dto;

import com.example.products.models.Characteristic;
import com.example.products.models.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductInfoDTO {
    private long id;
    private String title;
    private String description;
    private double price;
    private HashMap<String, String> characteristics;

    public ProductInfoDTO(Product product){
        this.id = product.getId();
        this.title = product.getTitle();
        this.description = product.getDescription();
        this.price = product.getPrice();

        var map = new HashMap<String, String>();
        for(Characteristic characteristic: product.getCharacteristics()){
            map.put(characteristic.getCharacteristic().getTitle(), characteristic.getValue());
        }
        this.characteristics = map;
    }
}
