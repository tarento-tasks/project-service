package com.example.ProjectService.config;

import com.example.ProjectService.Model.InvalidatedToken;

import com.example.ProjectService.Repository.InvalidatedTokenRepository;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.util.UUID;
import jakarta.annotation.PostConstruct; // For Spring Boot 3.x
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.List;

@Component
public class JwtUtil {

    private Key secretKeyDecoded;
    private static final long EXPIRATION_TIME = 86400000; 

    private final InvalidatedTokenRepository invalidatedTokenRepository;

    @Value("${jwt.secret}")
    private String secretKey;

    public JwtUtil(InvalidatedTokenRepository invalidatedTokenRepository) {
        this.invalidatedTokenRepository = invalidatedTokenRepository;
    }

    public Boolean validateToken(String token) {
        return (!isTokenExpired(token) && !isTokenInvalid(token));
    }

    @PostConstruct
    public void init() {
        if (secretKey == null || secretKey.length() < 32) {
            throw new IllegalArgumentException("JWT Secret Key must be at least 32 bytes long");
        }
        this.secretKeyDecoded = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    
    public String generateToken(User user) {
        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("authorities", List.of("ROLE_" + user.getRole().getRoleName()))
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(secretKeyDecoded, SignatureAlgorithm.HS256)
                .compact();
    }

    
    public String extractUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKeyDecoded)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    
    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKeyDecoded)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    
    public UUID extractUserId(String token) {
        Claims claims = extractAllClaims(token);
        String userIdStr = claims.get("user_id", String.class); 
    
        if (userIdStr == null || userIdStr.trim().isEmpty()) {
            throw new IllegalArgumentException("User ID is missing from token");
        }
    
        return UUID.fromString(userIdStr);
    }
    
    public Boolean validateToken(String token, String email) {
        final String extractedEmail = extractUsername(token);
        return (extractedEmail.equals(email) && !isTokenExpired(token) && !isTokenInvalid(token));
    }

    private Boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    
    public void invalidateToken(String token) {
        System.out.println("Invalidating token: " + token);
        Date expirationDate = extractAllClaims(token).getExpiration();
        InvalidatedToken invalidatedToken = new InvalidatedToken(token, expirationDate);
        invalidatedTokenRepository.save(invalidatedToken);
    }

    
    public boolean isTokenInvalid(String token) {
        return invalidatedTokenRepository.findByToken(token).isPresent();
    }
}