// package com.example.ProjectService.Controller;

// import com.example.ProjectService.DTO.ApiResponse;
// import com.example.ProjectService.DTO.ProjectEnrollmentDto;
// import com.example.ProjectService.DTO.UserDTO;
// import com.example.ProjectService.Model.Project;
// import com.example.ProjectService.Model.ProjectEnrollment;
// import com.example.ProjectService.Service.ProjectEnrollmentService;

// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.security.access.prepost.PreAuthorize;
// import org.springframework.security.core.Authentication;
// import org.springframework.security.core.context.SecurityContextHolder;
// import org.springframework.web.bind.annotation.*;

// import java.util.List;
// import java.util.UUID;

// @RestController
// @CrossOrigin(origins = "http://localhost:5173")
// @RequestMapping("/api/project-enrollment")
// public class ProjectEnrollmentController {

//     private final ProjectEnrollmentService enrollmentService;

//     public ProjectEnrollmentController(ProjectEnrollmentService enrollmentService) {
//         this.enrollmentService = enrollmentService;
//     }

//     @GetMapping("/approved-students")
//     @PreAuthorize("hasAnyRole('ADMIN', 'MENTOR')")
//     public ResponseEntity<ApiResponse<List<UserDTO>>> getApprovedStudentsForProject(
//             @RequestParam UUID projectId) {
//         List<UserDTO> students = enrollmentService.getApprovedStudentsForProject(projectId);
//         return ResponseEntity.ok(
//             new ApiResponse<>(HttpStatus.OK.value(), "Approved students fetched successfully", students)
//         );
//     }

//     @PostMapping
//     @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
//     public ResponseEntity<ApiResponse<ProjectEnrollment>> createOrUpdateEnrollment(
//             @RequestParam(value = "enrollmentId", required = false) UUID enrollmentId,
//             @RequestBody ProjectEnrollmentDto dto
//     ) {
//         try {
//             ProjectEnrollment enrollment;
//             if (enrollmentId != null) {
//                 enrollment = enrollmentService.updateEnrollmentStatus(enrollmentId, dto.getStatus());
//                 return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Enrollment status updated successfully", enrollment));
//             } else {
//                 enrollment = enrollmentService.enrollStudent(dto);
//                 return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Enrollment created successfully", enrollment));
//             }
//         } catch (IllegalStateException e) {
//             return ResponseEntity.badRequest().body(new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), e.getMessage(), null));
//         }
//     }

//     @GetMapping
//     @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT')")
//     public ResponseEntity<ApiResponse<?>> getEnrollments(
//             @RequestParam(value = "enrollmentId", required = false) UUID enrollmentId,
//             @RequestParam(value = "studentId", required = false) UUID studentId,
//             @RequestParam(value = "status", required = false) String status
//     ) {
//         Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//         UUID currentUserId = UUID.fromString(authentication.getName()); // Assuming UUID is stored in subject
//         String role = authentication.getAuthorities().iterator().next().getAuthority(); // e.g., "ROLE_STUDENT"

//         if (enrollmentId != null) {
//             ProjectEnrollment enrollment = enrollmentService.getEnrollmentById(enrollmentId);
//             return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Enrollment fetched successfully", enrollment));
//         }

//         if ("ROLE_STUDENT".equals(role)) {
//             if ("APPROVED".equalsIgnoreCase(status)) {
//                 List<Project> approvedProjects = enrollmentService.getApprovedProjectsForStudent(currentUserId);
//                 return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Approved projects fetched successfully", approvedProjects));
//             } else {
//                 List<ProjectEnrollment> enrollments = enrollmentService.getAllEnrollmentsForStudent(currentUserId);
//                 return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "All enrollments fetched successfully", enrollments));
//             }
//         }

//         if ("ROLE_ADMIN".equals(role)) {
//             if (studentId != null) {
//                 List<ProjectEnrollment> enrollments = enrollmentService.getAllEnrollmentsForStudent(studentId);
//                 return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Student enrollments fetched successfully", enrollments));
//             }
//             List<ProjectEnrollment> enrollments = enrollmentService.getAllEnrollments();
//             return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "All enrollments fetched successfully", enrollments));
//         }

//         return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
//                 new ApiResponse<>(HttpStatus.FORBIDDEN.value(), "Access denied", null)
//         );
//     }

//     @DeleteMapping("/{enrollmentId}")
//     @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT')")
//     public ResponseEntity<ApiResponse<Void>> softDeleteEnrollment(@PathVariable UUID enrollmentId) {
//         enrollmentService.softDeleteEnrollment(enrollmentId);
//         return ResponseEntity.ok(
//                 new ApiResponse<>(HttpStatus.OK.value(), "Enrollment soft deleted successfully", null)
//         );
//     }
// }
