package com.example.authorization.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class Authorization {
    private String email;
    private String password;
}
