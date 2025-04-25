// package com.example.ProjectService.config;

// import io.jsonwebtoken.Claims;
// import io.jsonwebtoken.Jwts;
// import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
// import org.springframework.security.core.authority.SimpleGrantedAuthority;
// import org.springframework.security.core.context.SecurityContextHolder;
// import org.springframework.web.filter.OncePerRequestFilter;

// import jakarta.servlet.FilterChain;
// import jakarta.servlet.ServletException;
// import jakarta.servlet.http.HttpServletRequest;
// import jakarta.servlet.http.HttpServletResponse;
// import java.io.IOException;
// import java.util.List;
// import java.util.stream.Collectors;

// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;




// public class JWTFilter extends OncePerRequestFilter {

//     private final JwtUtil jwtUtil;
//     private static final Logger logger = LoggerFactory.getLogger(JWTFilter.class);

//     public JWTFilter(JwtUtil jwtUtil) {
//         this.jwtUtil = jwtUtil;
//     }

//     @Override
//     protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
//             throws ServletException, IOException {
        
//         String header = request.getHeader("Authorization");

//         // Skip filter if no Authorization header
//         if (header == null || !header.startsWith("Bearer ")) {
//             filterChain.doFilter(request, response);
//             return;
//         }

//         String token = header.substring(7); // Remove "Bearer " prefix

//         // Validate token
//         try {
//             if (jwtUtil.isTokenInvalid(token)) {
//                 response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
//                 return;
//             }

//             Claims claims = jwtUtil.extractAllClaims(token);
//             String username = claims.getSubject();
//             List<String> authorities = claims.get("authorities", List.class);

//             if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
//                 List<SimpleGrantedAuthority> grantedAuthorities = authorities.stream()
//                         .map(SimpleGrantedAuthority::new)
//                         .collect(Collectors.toList());

//                 UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
//                         username,
//                         null,
//                         grantedAuthorities
//                 );
//                 SecurityContextHolder.getContext().setAuthentication(auth);
                
//                 logger.debug("Authenticated user: {} with roles: {}", username, authorities);
//             }
//         } catch (Exception e) {
//             SecurityContextHolder.clearContext();
//             logger.error("JWT validation failed: {}", e.getMessage());
//             response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
//             return;
//         }

//         filterChain.doFilter(request, response);
//     }
// }