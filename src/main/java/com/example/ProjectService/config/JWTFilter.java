package com.example.ProjectService.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JWTFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JWTFilter.class);
    private static final String AUTH_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    
    private final JwtUtil jwtUtil;

    public JWTFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                   HttpServletResponse response, 
                                   FilterChain filterChain)
            throws ServletException, IOException {
        
        try {
            String authHeader = request.getHeader(AUTH_HEADER);
            
            if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
                logger.debug("No JWT token found in request headers");
                filterChain.doFilter(request, response);
                return;
            }

            String token = authHeader.substring(BEARER_PREFIX.length());
            logger.debug("Processing JWT token: {}", token);

            if (jwtUtil.isTokenInvalid(token)) {
                logger.warn("Attempted use of invalidated token");
                sendError(response, HttpServletResponse.SC_UNAUTHORIZED, 
                        "Token has been invalidated. Please log in again.");
                return;
            }

            if (!jwtUtil.validateToken(token)) {
                logger.warn("Invalid JWT token");
                sendError(response, HttpServletResponse.SC_UNAUTHORIZED, 
                        "Invalid token. Please log in again.");
                return;
            }

            Claims claims = jwtUtil.getClaims(token);
            String username = claims.getSubject();
            List<String> authorities = claims.get("authorities", List.class);

            if (username != null) {
                logger.debug("Authenticating user: {} with roles: {}", username, authorities);
                
                List<SimpleGrantedAuthority> grantedAuthorities = authorities.stream()
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

                UsernamePasswordAuthenticationToken authentication = 
                    new UsernamePasswordAuthenticationToken(
                        username, 
                        null, 
                        grantedAuthorities);
                
                SecurityContextHolder.getContext().setAuthentication(authentication);
                logger.info("Authenticated user: {}", username);
            }
            
        } catch (ExpiredJwtException ex) {
            logger.warn("Expired JWT token: {}", ex.getMessage());
            sendError(response, HttpServletResponse.SC_UNAUTHORIZED, 
                    "Token has expired. Please log in again.");
            return;
        } catch (SignatureException ex) {
            logger.warn("Invalid JWT signature: {}", ex.getMessage());
            sendError(response, HttpServletResponse.SC_UNAUTHORIZED, 
                    "Invalid token signature.");
            return;
        } catch (Exception ex) {
            logger.error("JWT processing error: {}", ex.getMessage(), ex);
            sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                    "An error occurred while processing your request.");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private void sendError(HttpServletResponse response, int status, String message) 
            throws IOException {
        SecurityContextHolder.clearContext();
        response.setStatus(status);
        response.setContentType("application/json");
        response.getWriter().write(String.format("{\"error\": \"%s\"}", message));
        response.getWriter().flush();
    }
}