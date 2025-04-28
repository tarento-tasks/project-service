package com.example.ProjectService.config;

import com.example.ProjectService.Model.InvalidatedToken;
import com.example.ProjectService.Repository.InvalidatedTokenRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.List;
import jakarta.annotation.PostConstruct;

@Component
public class JwtUtil {
    private Key secretKeyDecoded;
    private static final long EXPIRATION_TIME = 86400000; // 1 day

    private final InvalidatedTokenRepository invalidatedTokenRepository;

    @Value("${jwt.secret}")
    private String secretKey;

    public JwtUtil(InvalidatedTokenRepository invalidatedTokenRepository) {
        this.invalidatedTokenRepository = invalidatedTokenRepository;
    }

    @PostConstruct
    public void init() {
        if (secretKey == null || secretKey.length() < 32) {
            throw new IllegalArgumentException("JWT Secret Key must be at least 32 bytes long");
        }
        this.secretKeyDecoded = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(String email, String role) {
        return Jwts.builder()
                .setSubject(email)
                .claim("authorities", List.of("ROLE_" + role)) // Add ROLE_ prefix here
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(secretKeyDecoded, SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractUsername(String token) {
        return getClaims(token).getSubject();
    }

    public Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKeyDecoded)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Boolean validateToken(String token) {
        return !isTokenExpired(token) && !isTokenInvalid(token);
    }

    private Boolean isTokenExpired(String token) {
        return getClaims(token).getExpiration().before(new Date());
    }

    public void invalidateToken(String token) {
        Date expirationDate = getClaims(token).getExpiration();
        InvalidatedToken invalidatedToken = new InvalidatedToken(token, expirationDate);
        invalidatedTokenRepository.save(invalidatedToken);
    }

    public boolean isTokenInvalid(String token) {
        return invalidatedTokenRepository.findByToken(token).isPresent();
    }
}