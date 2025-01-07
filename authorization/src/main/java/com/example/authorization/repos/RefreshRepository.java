package com.example.authorization.repos;

import com.example.authorization.models.Refresh;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshRepository extends CrudRepository<Refresh, Long> {
    Optional<Refresh> findByEmail(String email);
}
