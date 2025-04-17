package com.example.ProjectService.Client;

import com.example.ProjectService.DTO.ApiResponse;
import com.example.ProjectService.DTO.SkillDTO;
import com.example.ProjectService.DTO.UserDTO;
import com.example.ProjectService.DTO.SkillMappingRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class UserServiceClient {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${external.user-service.base-url}")
    private String userServiceBaseUrl;

    // User methods
    public UserDTO getUserById(UUID userId) {
        String url = UriComponentsBuilder.fromUriString(userServiceBaseUrl + "/api/users")
                .queryParam("userId", userId)
                .toUriString();

        ResponseEntity<ApiResponse<UserDTO>> response = restTemplate.exchange(
            url,
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<ApiResponse<UserDTO>>() {}
        );
        
        if (response.getBody() != null && response.getBody().getResponse() != null) {
            return response.getBody().getResponse();
        }
        return null;
    }

    public UserDTO getUserByEmail(String email) {
        String url = UriComponentsBuilder.fromUriString(userServiceBaseUrl + "/api/users")
                .queryParam("email", email)
                .toUriString();

        ResponseEntity<ApiResponse<UserDTO>> response = restTemplate.exchange(
            url,
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<ApiResponse<UserDTO>>() {}
        );
        
        if (response.getBody() != null && response.getBody().getResponse() != null) {
            return response.getBody().getResponse();
        }
        return null;
    }

    public List<UserDTO> getUsersByRole(String role) {
        String url = UriComponentsBuilder.fromUriString(userServiceBaseUrl + "/api/users")
                .queryParam("role", role)
                .toUriString();

        ResponseEntity<ApiResponse<List<UserDTO>>> response = restTemplate.exchange(
            url,
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<ApiResponse<List<UserDTO>>>() {}
        );
        
        if (response.getBody() != null && response.getBody().getResponse() != null) {
            return response.getBody().getResponse();
        }
        return List.of();
    }

    public List<UserDTO> getAllUsers() {
        String url = userServiceBaseUrl + "/api/users";
        ResponseEntity<ApiResponse<List<UserDTO>>> response = restTemplate.exchange(
            url,
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<ApiResponse<List<UserDTO>>>() {}
        );
        
        if (response.getBody() != null && response.getBody().getResponse() != null) {
            return response.getBody().getResponse();
        }
        return List.of();
    }

    // Skill methods
    public SkillDTO getSkillById(UUID skillId) {
        String url = UriComponentsBuilder.fromUriString(userServiceBaseUrl + "/api/skills")
                .queryParam("id", skillId)
                .toUriString();

        ResponseEntity<ApiResponse<SkillDTO>> response = restTemplate.exchange(
            url,
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<ApiResponse<SkillDTO>>() {}
        );
        
        if (response.getBody() != null && response.getBody().getResponse() != null) {
            return response.getBody().getResponse();
        }
        return null;
    }

    public List<SkillDTO> getAllSkills() {
        String url = userServiceBaseUrl + "/api/skills";
        ResponseEntity<ApiResponse<List<SkillDTO>>> response = restTemplate.exchange(
            url,
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<ApiResponse<List<SkillDTO>>>() {}
        );
        
        if (response.getBody() != null && response.getBody().getResponse() != null) {
            return response.getBody().getResponse();
        }
        return List.of();
    }

    // Skill Mapping methods
    public List<SkillMappingRequest> getSkillMappingsForUser(UUID userId) {
        String url = UriComponentsBuilder.fromUriString(userServiceBaseUrl + "/api/skill-mapping")
                .queryParam("userId", userId)
                .toUriString();

        ResponseEntity<ApiResponse<List<SkillMappingRequest>>> response = restTemplate.exchange(
            url,
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<ApiResponse<List<SkillMappingRequest>>>() {}
        );
        
        if (response.getBody() != null && response.getBody().getResponse() != null) {
            return response.getBody().getResponse();
        }
        return List.of();
    }

    public List<UUID> getSkillIdsForUser(UUID userId) {
        List<SkillMappingRequest> mappings = getSkillMappingsForUser(userId);
        return mappings.stream()
                .map(SkillMappingRequest::getSkillId)
                .collect(Collectors.toList());
    }

    public SkillMappingRequest addSkillToUser(UUID userId, UUID skillId) {
        String url = userServiceBaseUrl + "/api/skill-mapping/users/" + userId + "/skills/" + skillId;
        ResponseEntity<ApiResponse<SkillMappingRequest>> response = restTemplate.exchange(
            url,
            HttpMethod.POST,
            null,
            new ParameterizedTypeReference<ApiResponse<SkillMappingRequest>>() {}
        );
        
        if (response.getBody() != null && response.getBody().getResponse() != null) {
            return response.getBody().getResponse();
        }
        return null;
    }

    public void removeSkillFromUser(UUID userId, UUID skillId) {
        String url = userServiceBaseUrl + "/api/skill-mapping/" + userId + "/" + skillId;
        restTemplate.exchange(
            url,
            HttpMethod.DELETE,
            null,
            Void.class
        );
    }
    // 🧠 1. Get Recommended Students Based on Skills and Project ID
    public List<UUID> getRecommendedStudents(List<UUID> skillIds, UUID projectId) {
        String url = UriComponentsBuilder
                .fromUriString(userServiceBaseUrl + "/api/skill-mapping/recommend-students")
                .queryParam("skillIds", skillIds.stream()
                        .map(UUID::toString)
                        .collect(Collectors.joining(",")))
                .queryParam("projectId", projectId.toString())
                .toUriString();
    
        ResponseEntity<ApiResponse<List<UUID>>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );
    
        if (response.getBody() != null && response.getBody().getResponse() != null) {
            return response.getBody().getResponse();
        }
        return List.of();
    }
    

// 🧠 2. Get Skill Mappings by Project ID

    public List<SkillMappingRequest> getSkillMappingsByProjectId(UUID projectId) {
    
    String url = userServiceBaseUrl + "/api/skill-mapping/project/" +projectId.toString();

    ResponseEntity<ApiResponse<List<SkillMappingRequest>>> response = restTemplate.exchange(
            url,
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<ApiResponse<List<SkillMappingRequest>>>() {}
    );

    if (response.getBody() != null && response.getBody().getResponse() != null) {

        
        return response.getBody().getResponse();
    }
    return List.of();
}

// 🧠 3. Get Skill Mappings by User ID (Already present, just clarifying)
public List<SkillMappingRequest> getSkillMappingsByUserId(UUID userId) {
    String url = userServiceBaseUrl + "/api/skill-mapping/user/" + userId;

    ResponseEntity<ApiResponse<List<SkillMappingRequest>>> response = restTemplate.exchange(
            url,
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<ApiResponse<List<SkillMappingRequest>>>() {}
    );

    if (response.getBody() != null && response.getBody().getResponse() != null) {
        return response.getBody().getResponse();
    }
    return List.of();
}

}