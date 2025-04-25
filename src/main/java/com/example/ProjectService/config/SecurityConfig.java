// package com.example.ProjectService.config;

// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.http.HttpMethod;
// import org.springframework.security.authentication.AuthenticationManager;
// import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
// import org.springframework.security.config.annotation.web.builders.HttpSecurity;
// import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
// import org.springframework.security.config.http.SessionCreationPolicy;
// import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
// import org.springframework.security.crypto.password.PasswordEncoder;
// import org.springframework.security.web.SecurityFilterChain;
// import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
// import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
// import org.springframework.web.cors.CorsConfiguration;
// import org.springframework.web.cors.CorsConfigurationSource;
// import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

// import java.util.Arrays;
// import java.util.List;

// @Configuration
// @EnableWebSecurity
// public class SecurityConfig {

//     private final JwtUtil jwtUtil;

//     public SecurityConfig(JwtUtil jwtUtil) {
//         this.jwtUtil = jwtUtil;
//     }

//     @Bean
//     public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//         http
//                 .cors(cors -> cors.configurationSource(corsConfigurationSource())) // Enable CORS
//                 .csrf(csrf -> csrf.disable()) 
//                 .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) 
//                 .authorizeHttpRequests(auth -> auth
//                         .requestMatchers("/api/auth/**").permitAll()
//                         .requestMatchers(HttpMethod.POST, "/api/auth/logout").authenticated()
//                         .requestMatchers("/api/roles/**").hasRole("ADMIN")
//                         .requestMatchers("/api/users/**").hasAnyRole("ADMIN","MENTOR","STUDENT")
//                         .requestMatchers(HttpMethod.POST, "/api/projects").hasRole( "ADMIN") 
//                         .requestMatchers("/api/projects/**").hasAnyRole("ADMIN", "MENTOR","STUDENT") 
                        

                        
//                         .requestMatchers("/api/tasks/**").hasAnyRole("ADMIN", "MENTOR", "STUDENT")
//                         .requestMatchers("/api/skills/**").hasAnyRole("ADMIN","MENTOR","STUDENT")
//                         .requestMatchers("/api/skill-mapping/**").hasAnyRole("ADMIN","MENTOR","STUDENT") 
//                         .requestMatchers(HttpMethod.POST, "/api/project-enrollment").hasAnyRole("STUDENT", "ADMIN")  
//                         .requestMatchers(HttpMethod.GET, "/api/project-enrollment").hasAnyRole("ADMIN", "STUDENT","MENTOR")  
                        
//                         .requestMatchers(HttpMethod.DELETE, "/api/project-enrollment/**").hasAnyRole("STUDENT", "ADMIN")  
//                         .requestMatchers("/api/stu-task/**").hasAnyRole("ADMIN","MENTOR","STUDENT") 
//                         .requestMatchers("/api/tasks/*/comments").hasAnyRole("ADMIN","MENTOR","STUDENT")
//                         .requestMatchers("/api/tasks/*/feedback").hasAnyRole("ADMIN","MENTOR","STUDENT")
//                         .requestMatchers(HttpMethod.GET, "/api/project-skills/recommendations/**").hasAnyRole("STUDENT", "MENTOR", "ADMIN")
//                         .requestMatchers(HttpMethod.POST, "/api/project-skills").hasRole("ADMIN")
//                         .requestMatchers(HttpMethod.GET, "/api/project-skills").hasAnyRole("STUDENT", "MENTOR", "ADMIN")
//                         .anyRequest().authenticated()
//                 )
                
//                 .sessionManagement(session -> session
//                 .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//             )
//             .addFilterBefore(new JWTFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class)
//             .exceptionHandling(ex -> ex
//                 .authenticationEntryPoint(new Http403ForbiddenEntryPoint())
//             );
        
    

//         return http.build();
//     }

//     @Bean
//     public CorsConfigurationSource corsConfigurationSource() {
//         CorsConfiguration configuration = new CorsConfiguration();
//         configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173", "http://localhost:3000"));

//         configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS")); // Allowed HTTP methods
//         configuration.setAllowedHeaders(List.of("*")); // Allow all headers
//         configuration.setAllowCredentials(true); // Allow credentials
        
//         UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//         source.registerCorsConfiguration("/**", configuration); // Apply to all paths
//         return source;
//     }

//     @Bean
//     public PasswordEncoder passwordEncoder() {
//         return new BCryptPasswordEncoder();
//     }

//     @Bean
//     public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
//         return config.getAuthenticationManager();
//     }
// }

