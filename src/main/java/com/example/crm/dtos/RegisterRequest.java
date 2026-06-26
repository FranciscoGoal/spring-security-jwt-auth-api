package com.example.crm.dtos;

public record RegisterRequest(
    String username,
    String email,
    String password,
    String confirmPassword
) {
}
