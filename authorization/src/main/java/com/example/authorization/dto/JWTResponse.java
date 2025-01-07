package com.example.authorization.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class JWTResponse {
    private String accessToken;
    private String refreshToken;
}
