package com.example.crm.services;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    private final JwtEncoder jwtEncoder;
    private final Duration tokenExpiration;

    public JwtService(
        JwtEncoder jwtEncoder,
        @Value("${app.jwt.expiration-minutes}") long expirationMinutes
    ) {
        this.jwtEncoder = jwtEncoder;
        this.tokenExpiration = Duration.ofMinutes(expirationMinutes);
    }

    public String generateToken(Authentication authentication) {
        Instant now = Instant.now();
        Instant expiresAt = now.plus(tokenExpiration);

        List<String> roles = authentication.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .filter(authority -> authority.startsWith("ROLE_"))
            .map(authority -> authority.substring("ROLE_".length()))
            .toList();

        JwsHeader headers = JwsHeader.with(MacAlgorithm.HS256).build();
        JwtClaimsSet claims = JwtClaimsSet.builder()
            .issuer("crm")
            .subject(authentication.getName())
            .issuedAt(now)
            .expiresAt(expiresAt)
            .claim("roles", roles)
            .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(headers, claims)).getTokenValue();
    }

    public long getExpiresInSeconds() {
        return tokenExpiration.toSeconds();
    }
}
