package com.example.authorization.security;

import com.example.authorization.dto.Authorization;
import com.example.authorization.dto.JWTResponse;
import com.example.authorization.dto.Registration;
import com.example.authorization.exceptions.AuthException;
import com.example.authorization.models.Refresh;
import com.example.authorization.models.User;
import com.example.authorization.repos.RefreshRepository;
import com.example.authorization.repos.UserRepository;
import io.jsonwebtoken.Claims;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@NoArgsConstructor
public class AuthService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RefreshRepository refreshRepository;
    @Autowired
    private JwtProvider provider;

    public JWTResponse login(@NonNull Authorization auth) throws AuthException{
        User user = userRepository.findByEmail(auth.getEmail()).orElseThrow(() -> new AuthException("User does not exists"));
        if(user.getPassword().equals(auth.getPassword())){
            String accessToken = provider.generateAccessToken(user);
            String refreshToken = provider.generateRefreshToken(user);
            Optional<Refresh> refresh = refreshRepository.findByEmail(auth.getEmail());
            Refresh refreshObj;
            if(refresh.isPresent()){
                refreshObj = refresh.get();
                refreshObj.setRefreshToken(refreshToken);
            }
            else{
                refreshObj = new Refresh(user.getEmail(), refreshToken);
            }
            refreshRepository.save(refreshObj);
            return new JWTResponse(accessToken, refreshToken);
        }
        else throw new AuthException("Wrong password");
    }

    public JWTResponse login(@NonNull Registration auth) throws AuthException{
        if(userRepository.existsUserByEmail(auth.getEmail())) throw new AuthException("Email already taken");
        if(userRepository.existsUserByPhone(auth.getPhone())) throw new AuthException("Phone already taken");
        UUID uuid = UUID.randomUUID();
        while(userRepository.existsById(uuid)) uuid = UUID.randomUUID();
        User user = new User(uuid, auth.getUsername(), auth.getPhone(), auth.getEmail(), auth.getPassword());
        userRepository.save(user);
        String accessToken = provider.generateAccessToken(user);
        String refreshToken = provider.generateRefreshToken(user);
        Refresh refresh = new Refresh(user.getEmail(), refreshToken);
        refreshRepository.save(refresh);
        return new JWTResponse(accessToken, refreshToken);
    }

    public JWTResponse getAccessToken(@NonNull String refreshToken) throws AuthException{
        if(provider.validateRefreshToken(refreshToken)){
            Claims claims = provider.getRefreshClaims(refreshToken);
            String email = claims.getSubject();
            Refresh refresh = refreshRepository.findByEmail(email).orElseThrow(() -> new AuthException("User`s refresh token does not exists"));
            String savedToken = refresh.getRefreshToken();
            if(refreshToken.equals(savedToken)){
                User user = userRepository.findByEmail(email).orElseThrow(() -> new AuthException("User does not exists"));
                String accessToken = provider.generateAccessToken(user);
                return new JWTResponse(accessToken, null);
            }
        }
        return new JWTResponse(null, null);
    }

    public JWTResponse refresh(@NonNull String refreshToken) throws AuthException{
        if(provider.validateRefreshToken(refreshToken)){
            Claims claims = provider.getRefreshClaims(refreshToken);
            String email = claims.getSubject();
            Refresh refresh = refreshRepository.findByEmail(email).orElseThrow(() -> new AuthException("User`s refresh token does not exists"));
            String savedToken = refresh.getRefreshToken();
            if(refreshToken.equals(savedToken)){
                User user = userRepository.findByEmail(email).orElseThrow(() -> new AuthException("User does not exists"));
                String accessToken = provider.generateAccessToken(user);
                String newRefreshToken = provider.generateRefreshToken(user);
                refresh.setRefreshToken(newRefreshToken);
                refreshRepository.save(refresh);
                return new JWTResponse(accessToken, newRefreshToken);
            }
        }
        return new JWTResponse(null, null);
    }
}
