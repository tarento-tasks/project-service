// package com.example.ProjectService.config;

// import com.example.ProjectService.Model.InvalidatedToken;

// import com.example.ProjectService.Repository.InvalidatedTokenRepository;

// import io.jsonwebtoken.Claims;
// import io.jsonwebtoken.Jwts;
// import io.jsonwebtoken.SignatureAlgorithm;
// import io.jsonwebtoken.security.Keys;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.stereotype.Component;
// import java.util.UUID;
// import jakarta.annotation.PostConstruct; // For Spring Boot 3.x
// import java.nio.charset.StandardCharsets;
// import java.security.Key;
// import java.util.Date;
// import java.util.List;






// @Component
// public class JwtUtil {

//     private Key secretKey;
//     private static final long EXPIRATION_TIME = 86400000; // 24 hours

//     @Value("${jwt.secret}")
//     private String secretKeyString;

//     @PostConstruct
//     public void init() {
//         if (secretKeyString == null || secretKeyString.length() < 32) {
//             throw new IllegalArgumentException("JWT Secret Key must be at least 32 bytes long");
//         }
//         this.secretKey = Keys.hmacShaKeyFor(secretKeyString.getBytes(StandardCharsets.UTF_8));
//     }

//     public Claims extractAllClaims(String token) {
//         return Jwts.parserBuilder()
//                 .setSigningKey(secretKey)
//                 .build()
//                 .parseClaimsJws(token)
//                 .getBody();
//     }

//     public String extractUsername(String token) {
//         return extractAllClaims(token).getSubject();
//     }

//     public UUID extractUserId(String token) {
//         return UUID.fromString(extractAllClaims(token).get("user_id", String.class));
//     }

//     public boolean isTokenExpired(String token) {
//         return extractAllClaims(token).getExpiration().before(new Date());
//     }

//     public boolean isTokenInvalid(String token) {
//         // In a microservice architecture, you might want to call the User Service
//         // to check if the token is invalidated, or use a shared Redis cache
//         // For simplicity, we're just checking expiration here
//         return isTokenExpired(token);
//     }

//     // This method would typically only be in the User Service
//     // Included here for completeness, but you might want to remove it
//     public String generateToken(String email, UUID userId, List<String> roles) {
//         return Jwts.builder()
//                 .setSubject(email)
//                 .claim("user_id", userId.toString())
//                 .claim("authorities", roles)
//                 .setIssuedAt(new Date())
//                 .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
//                 .signWith(secretKey, SignatureAlgorithm.HS256)
//                 .compact();
//     }
// }