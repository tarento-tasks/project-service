// package com.example.ProjectService.Service;

// import com.example.ProjectService.DTO.ProjectEnrollmentDto;
// import com.example.ProjectService.DTO.UserDTO;
// import com.example.ProjectService.Model.Project;
// import com.example.ProjectService.Model.ProjectEnrollment;
// import com.example.ProjectService.Model.UserRef;
// import com.example.ProjectService.Repository.ProjectEnrollmentRepository;
// import com.example.ProjectService.Repository.ProjectRepository;

// import org.springframework.security.access.AccessDeniedException;
// import org.springframework.security.core.Authentication;
// import org.springframework.security.core.context.SecurityContextHolder;
// import org.springframework.stereotype.Service;
// import org.springframework.web.client.RestTemplate;

// import java.time.LocalDate;
// import java.time.LocalDateTime;
// import java.util.HashMap;
// import java.util.List;
// import java.util.UUID;
// import java.util.stream.Collectors;


// import com.example.ProjectService.Model.ProjectEnrollment;
// import com.example.ProjectService.Repository.ProjectEnrollmentRepository;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.stereotype.Service;
// import org.springframework.web.client.RestTemplate;

// import java.util.*;
// import java.util.stream.Collectors;

// @Service
// public class ProjectEnrollmentService {

//     @Autowired
//     private ProjectEnrollmentRepository enrollmentRepository;

//     @Autowired
//     private RestTemplate restTemplate;

//     @Value("${user.service.url}")
//     private String userServiceBaseUrl;


//     public ProjectEnrollment enrollStudent(ProjectEnrollmentDto dto) {
//         Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//         UUID studentId = UUID.fromString(authentication.getName());
//         String role = authentication.getAuthorities().iterator().next().getAuthority();

//         if (!"ROLE_STUDENT".equals(role)) {
//             throw new AccessDeniedException("Only students can enroll in projects.");
//         }

//         Project project = projectRepository.findById(dto.getProjectId())
//                 .orElseThrow(() -> new RuntimeException("Project not found"));

//         if (project.getDeletedAt() != null || 
//             (project.getLastDate() != null && project.getLastDate().isBefore(LocalDate.now()))) {
//             throw new IllegalStateException("Cannot enroll in this project.");
//         }

//         // Check if active enrollment already exists
//         if (enrollmentRepository.existsByStudent_UserIdAndProject_ProjectIdAndDeletedAtIsNull(
//                 studentId, project.getProjectId())) {
//             throw new IllegalStateException("Already enrolled in this project.");
//         }

//         // Create and save new enrollment
//         UserRef studentRef = new UserRef();
//         studentRef.setUserId(studentId);

//         ProjectEnrollment enrollment = new ProjectEnrollment();
//         enrollment.setStudent(studentRef);
//         enrollment.setProject(project);
//         enrollment.setStatus("PENDING");
//         enrollment.setCreatedAt(LocalDateTime.now());

//         return enrollmentRepository.save(enrollment);
//     }

//     public ProjectEnrollment updateEnrollmentStatus(UUID enrollmentId, String status) {
//         ProjectEnrollment enrollment = getEnrollmentById(enrollmentId);
        
//         // Validate status transition if needed
//         if (!isValidStatusTransition(enrollment.getStatus(), status)) {
//             throw new IllegalStateException("Invalid status transition");
//         }
        
//         enrollment.setStatus(status);
//         enrollment.setModifiedAt(LocalDateTime.now());
//         return enrollmentRepository.save(enrollment);
//     }

//     private boolean isValidStatusTransition(String currentStatus, String newStatus) {
//         // Implement your status transition logic here
//         return true; // Simplified for example
//     }

//     public List<ProjectEnrollment> getAllEnrollments() {
//         return enrollmentRepository.findByDeletedAtIsNull();
//     }

//     public List<ProjectEnrollment> getAllEnrollmentsForStudent(UUID studentId) {
//         // Using the repository method that checks both enrollment and project deletion status
//         return enrollmentRepository.findByStudent_UserIdAndDeletedAtIsNullAndProject_DeletedAtIsNull(studentId);
//     }
    
//     public List<Project> getApprovedProjectsForStudent(UUID studentId) {
//         // Using the repository method that checks status and both deletion statuses
//         return enrollmentRepository
//                 .findByStudent_UserIdAndStatusAndDeletedAtIsNullAndProject_DeletedAtIsNull(studentId, "APPROVED")
//                 .stream()
//                 .map(ProjectEnrollment::getProject)
//                 .collect(Collectors.toList());
//     }

//     public List<Map<String, Object>> getApprovedStudentsForProject(UUID projectId) {
//         List<ProjectEnrollment> approvedEnrollments = enrollmentRepository
//                 .findByProject_ProjectIdAndStatusAndDeletedAtIsNull(projectId, "APPROVED");

//         return approvedEnrollments.stream()
//                 .map(enrollment -> {
//                     UUID studentId = enrollment.getStudent().getUserId();
//                     try {
//                         Map<String, Object> userResponse = restTemplate.getForObject(
//                             userServiceBaseUrl + "/api/users/" + studentId,
//                             Map.class);
                        
//                         // Ensure we always have at least the userId
//                         if (userResponse == null) {
//                             userResponse = new HashMap<>();
//                         }
//                         userResponse.put("userId", studentId);
//                         return userResponse;
//                     } catch (Exception e) {
//                         // Return minimal user info with just the ID if call fails
//                         Map<String, Object> fallback = new HashMap<>();
//                         fallback.put("userId", studentId);
//                         return fallback;
//                     }
//                 })
//                 .collect(Collectors.toList());
//     }
//     public ProjectEnrollment getEnrollmentById(UUID enrollmentId) {
//         return enrollmentRepository.findByIdAndDeletedAtIsNull(enrollmentId)
//                 .orElseThrow(() -> new RuntimeException("Enrollment not found"));
//     }

//     public void softDeleteEnrollment(UUID enrollmentId) {
//         ProjectEnrollment enrollment = getEnrollmentById(enrollmentId);
//         enrollment.setDeletedAt(LocalDateTime.now());
//         enrollmentRepository.save(enrollment);
//     }
// }

