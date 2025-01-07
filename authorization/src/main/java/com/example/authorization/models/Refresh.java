package com.example.authorization.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "refresh")
@NoArgsConstructor
public class Refresh {
    @Id
    @GeneratedValue
    private long id;
    @Column(unique = true)
    private String email;
    @Column(name = "token")
    private String refreshToken;

    public Refresh(String email, String refreshToken){
        this.email = email;
        this.refreshToken = refreshToken;
    }
}
