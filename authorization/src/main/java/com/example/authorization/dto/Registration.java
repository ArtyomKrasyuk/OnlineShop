package com.example.authorization.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Registration {
    private String username;
    private String phone;
    private String email;
    private String password;
}
