package com.example.cart.repos;

import com.example.cart.models.Product;
import com.example.cart.models.EmbeddedId;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends CrudRepository<Product, EmbeddedId> {
    Optional<List<Product>> findByUserId(UUID userId);
}
