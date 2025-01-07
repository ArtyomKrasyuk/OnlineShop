package com.example.authorization.controllers;

import com.example.authorization.dto.Authorization;
import com.example.authorization.dto.JWTResponse;
import com.example.authorization.dto.RefreshRequest;
import com.example.authorization.dto.Registration;
import com.example.authorization.exceptions.AuthException;
import com.example.authorization.security.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class MainController {
    @Autowired
    private AuthService service;

    @PostMapping("/registration")
    public JWTResponse registration(@RequestBody Registration regData){
        try{
            return service.login(regData);
        }
        catch (AuthException ex){
            log.info(ex.getMessage());
            return new JWTResponse(null, null);
        }
    }

    @PostMapping("/authorization")
    public JWTResponse authorization(@RequestBody Authorization authData){
        try{
            return service.login(authData);
        }
        catch (AuthException ex){
            log.info(ex.getMessage());
            return new JWTResponse(null, null);
        }
    }

    @PostMapping("/token")
    public JWTResponse getNewAccessToken(@RequestBody RefreshRequest refreshRequest){
        try{
            return service.getAccessToken(refreshRequest.getToken());
        }
        catch (AuthException ex){
            log.info(ex.getMessage());
            return new JWTResponse(null, null);
        }
    }

    @PostMapping("/refresh")
    public JWTResponse getNewRefreshToken(@RequestBody RefreshRequest refreshRequest){
        try{
            return service.refresh(refreshRequest.getToken());
        }
        catch (AuthException ex){
            log.info(ex.getMessage());
            return new JWTResponse(null, null);
        }
    }
}
