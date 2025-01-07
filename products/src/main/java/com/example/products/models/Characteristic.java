package com.example.products.models;

import jakarta.persistence.*;
import lombok.*;


@Data
@NoArgsConstructor
@Entity
@Table(name = "characteristic")
public class Characteristic {
    @EmbeddedId
    private CharacteristicKey id;
    @ManyToOne
    @MapsId("productId")
    @JoinColumn(name = "product_id")
    private Product product;
    @ManyToOne
    @MapsId("characteristicsId")
    @JoinColumn(name = "characteristics_id")
    private Characteristics characteristic;
    private String value;
}
