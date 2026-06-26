package com.example.crm.dtos;

public record LoginResponse(
    String accessToken,
    String tokenType,
    long expiresIn
) {
}
