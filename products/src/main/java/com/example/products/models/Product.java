package com.example.products.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Data
@NoArgsConstructor
@Entity
@Table(name = "product")
public class Product {
    @Id
    @GeneratedValue
    private long id;
    private String title;
    private String description;
    private double price;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", referencedColumnName = "id")
    private Category category;
    private int number;
    @OneToMany(mappedBy = "product")
    @EqualsAndHashCode.Exclude
    private Set<Characteristic> characteristics;
}
