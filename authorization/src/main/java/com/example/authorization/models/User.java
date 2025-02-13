package com.example.authorization.models;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="users")
public class User{
    public enum Role{
        USER,
        ADMIN
    }

    @Id
    private UUID id;
    private String username;
    @Column(unique = true)
    private String phone;
    @Column(unique = true)
    private String email;
    private String password;
    @Enumerated(EnumType.STRING)
    private Role role;
}
