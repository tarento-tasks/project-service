package com.example.ProjectService.DTO;

import lombok.Data;
import java.util.UUID;

@Data
public class UserDTO {
    private UUID userId;
    private String email;
    private String roleName; // "ADMIN", "MENTOR", "STUDENT"
    private String name; // Optional: add other minimal fields you need
    

}