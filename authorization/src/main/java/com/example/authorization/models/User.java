package com.example.authorization.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="users")
public class User {
    @Id
    private UUID id;
    private String username;
    @Column(unique = true)
    private String phone;
    @Column(unique = true)
    private String email;
    private String password;
}
