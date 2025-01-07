package com.example.products.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Data
@NoArgsConstructor
@Entity
@Table(name = "characteristics")
public class Characteristics {
    @Id
    @GeneratedValue
    private long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", referencedColumnName = "id")
    private Category category;
    private String title;
    @OneToMany(mappedBy = "characteristic")
    @EqualsAndHashCode.Exclude
    private Set<Characteristic> characteristics;
}
