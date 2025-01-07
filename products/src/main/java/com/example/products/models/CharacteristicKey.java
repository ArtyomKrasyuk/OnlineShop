package com.example.products.models;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Data
@NoArgsConstructor
@Embeddable
public class CharacteristicKey implements Serializable {
    @Column(name = "product_id")
    private long productId;
    @Column(name = "characteristics_id")
    private long characteristicsId;
}
