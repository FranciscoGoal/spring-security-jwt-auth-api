package com.example.crm.services;

import com.example.crm.dtos.LoginRequest;
import com.example.crm.dtos.LoginResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class LoginService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public LoginService(AuthenticationManager authenticationManager, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public LoginResponse login(LoginRequest request) {
            
        /*
            Chek if username and password inputs are not null 
         */ 

        if (request.getUsername() == null || request.getUsername().isBlank()) {
            throw new IllegalArgumentException("Username is required");
        }

        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new IllegalArgumentException("Password is required");
        }
        
        
        /*
            Generate a token for each session
                TODO: Sessions expires in 60 minutes. Fix that store the token with cookies 
         */ 
        UsernamePasswordAuthenticationToken token = UsernamePasswordAuthenticationToken.unauthenticated(
            request.getUsername().trim(),
            request.getPassword()
        );
        Authentication authentication = authenticationManager.authenticate(token);
        String accessToken = jwtService.generateToken(authentication);

        return new LoginResponse(accessToken, "Bearer", jwtService.getExpiresInSeconds());
    }
}
