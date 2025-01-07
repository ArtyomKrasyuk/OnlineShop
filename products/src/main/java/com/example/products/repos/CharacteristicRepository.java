package com.example.products.repos;

import com.example.products.models.Characteristic;
import com.example.products.models.CharacteristicKey;
import org.springframework.data.repository.CrudRepository;

public interface CharacteristicRepository extends CrudRepository<Characteristic, CharacteristicKey> {
}
