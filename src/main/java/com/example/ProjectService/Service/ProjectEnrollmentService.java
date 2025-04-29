// src/main/java/com/example/ProjectService/Service/ProjectEnrollmentService.java
package com.example.ProjectService.Service;

import com.example.ProjectService.DTO.ProjectEnrollmentDto;
import com.example.ProjectService.Model.Project;
import com.example.ProjectService.Model.ProjectEnrollment;
import com.example.ProjectService.Repository.ProjectEnrollmentRepository;
import com.example.ProjectService.Repository.ProjectRepository;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProjectEnrollmentService {

    private final ProjectEnrollmentRepository enrollmentRepository;
    private final ProjectRepository projectRepository;
    private final RestTemplate restTemplate;

    @Value("${user.service.url}")
    private String userServiceUrl;  // e.g. http://localhost:8081/api/users

    public ProjectEnrollmentService(ProjectEnrollmentRepository enrollmentRepository,
                                    ProjectRepository projectRepository,
                                    RestTemplate restTemplate) {
        this.enrollmentRepository = enrollmentRepository;
        this.projectRepository = projectRepository;
        this.restTemplate = restTemplate;
    }

    /** Enroll the authenticated student into a project */
    public ProjectEnrollment enrollStudent(ProjectEnrollmentDto dto) {
        UUID studentId = resolveStudentIdFromToken();

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String role = auth.getAuthorities().iterator().next().getAuthority();
        if (!"ROLE_STUDENT".equals(role)) {
            throw new AccessDeniedException("Only students can enroll in projects.");
        }

        Project project = projectRepository.findById(dto.getProjectId())
                .orElseThrow(() -> new RuntimeException("Project not found"));

        if (project.getDeletedAt() != null ||
            (project.getLastDate() != null && project.getLastDate().isBefore(LocalDate.now()))) {
            throw new IllegalStateException("Cannot enroll in this project.");
        }

        if (enrollmentRepository.existsByStudentIdAndProject_ProjectIdAndDeletedAtIsNull(
                studentId, project.getProjectId())) {
            throw new IllegalStateException("Already enrolled in this project.");
        }

        ProjectEnrollment e = new ProjectEnrollment();
        e.setStudentId(studentId);
        e.setProject(project);
        e.setStatus("PENDING");
        e.setCreatedAt(LocalDateTime.now());
        return enrollmentRepository.save(e);
    }

    /** Update status of an existing enrollment */
    public ProjectEnrollment updateEnrollmentStatus(UUID enrollmentId, String status) {
        ProjectEnrollment e = getEnrollmentById(enrollmentId);
        e.setStatus(status);
        e.setModifiedAt(LocalDateTime.now());
        return enrollmentRepository.save(e);
    }

    /** Fetch one enrollment by ID */
    public ProjectEnrollment getEnrollmentById(UUID enrollmentId) {
        return enrollmentRepository.findByEnrollmentIdAndDeletedAtIsNull(enrollmentId)
                .orElseThrow(() -> new RuntimeException("Enrollment not found"));
    }

    /** Soft-delete an enrollment */
    public void softDeleteEnrollment(UUID enrollmentId) {
        ProjectEnrollment e = getEnrollmentById(enrollmentId);
        e.setDeletedAt(LocalDateTime.now());
        enrollmentRepository.save(e);
    }

    /** Admin: fetch all enrollments */
    public List<ProjectEnrollment> getAllEnrollments() {
        return enrollmentRepository.findByDeletedAtIsNull();
    }

    /** Admin: fetch all enrollments for a given studentId */
    public List<ProjectEnrollment> getAllEnrollmentsForStudent(UUID studentId) {
        return enrollmentRepository.findByStudentIdAndDeletedAtIsNullAndProject_DeletedAtIsNull(studentId);
    }

    /** Admin: fetch approved projects (entity) for a given studentId */
    public List<Project> getApprovedProjectsForStudent(UUID studentId) {
        return enrollmentRepository
                .findByStudentIdAndStatusAndDeletedAtIsNullAndProject_DeletedAtIsNull(studentId, "APPROVED")
                .stream()
                .map(ProjectEnrollment::getProject)
                .collect(Collectors.toList());
    }

    /** Student: fetch their own enrollments, optionally filtering by status */
    public List<ProjectEnrollment> getAllEnrollmentsForCurrentStudent(String status) {
        UUID studentId = resolveStudentIdFromToken();
        if ("APPROVED".equalsIgnoreCase(status)) {
            // wrap each approved Project into a dummy Enrollment object if needed
            return getApprovedProjectsForStudent(studentId)
                    .stream()
                    .map(p -> {
                        ProjectEnrollment e = new ProjectEnrollment();
                        e.setStudentId(studentId);
                        e.setProject(p);
                        e.setStatus("APPROVED");
                        return e;
                    })
                    .collect(Collectors.toList());
        } else {
            return getAllEnrollmentsForStudent(studentId);
        }
    }

    private UUID resolveStudentIdFromToken() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
    
        // Propagate JWT
        String token = ((ServletRequestAttributes) RequestContextHolder
                        .currentRequestAttributes())
                        .getRequest()
                        .getHeader("Authorization");
        HttpHeaders headers = new HttpHeaders();
        if (token != null) headers.set("Authorization", token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Void> entity = new HttpEntity<>(headers);
    
        ResponseEntity<JsonNode> resp;
        try {
            resp = restTemplate.exchange(
                    userServiceUrl + "?email=" + email,
                    HttpMethod.GET,
                    entity,
                    JsonNode.class
            );
        } catch (HttpClientErrorException.NotFound nf) {
            throw new RuntimeException("User not found by email=" + email);
        }
    
        if (!resp.getStatusCode().is2xxSuccessful() || resp.getBody() == null) {
            throw new RuntimeException("Failed to fetch user details");
        }
    
        JsonNode responseBody = resp.getBody();
        
        // Check the actual response structure
        JsonNode responseNode = responseBody.path("response");
        if (responseNode.isMissingNode()) {
            throw new RuntimeException("User data not found in response. Full response: " + responseBody);
        }
    
        JsonNode idNode = responseNode.path("userId");
        if (idNode.isMissingNode() || idNode.asText().isEmpty()) {
            throw new RuntimeException("User ID not found in response. Full response: " + responseBody);
        }
        
        try {
            return UUID.fromString(idNode.asText());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid user ID format: " + idNode.asText());
        }
    }
}
