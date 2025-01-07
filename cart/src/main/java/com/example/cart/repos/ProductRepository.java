package com.example.cart.repos;

import com.example.cart.models.Product;
import com.example.cart.models.ProductId;
import org.springframework.data.repository.CrudRepository;

public interface ProductRepository extends CrudRepository<Product, ProductId> {
}
