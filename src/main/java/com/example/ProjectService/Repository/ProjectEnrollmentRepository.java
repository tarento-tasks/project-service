package com.example.ProjectService.Repository;

import com.example.ProjectService.Model.ProjectEnrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProjectEnrollmentRepository extends JpaRepository<ProjectEnrollment, UUID> {
    List<ProjectEnrollment> findByDeletedAtIsNull();
    
    List<ProjectEnrollment> findByStudentIdAndDeletedAtIsNullAndProject_DeletedAtIsNull(UUID studentId);
    
    List<ProjectEnrollment> findByStudentIdAndStatusAndDeletedAtIsNullAndProject_DeletedAtIsNull(
        UUID studentId, String status);
    
    List<ProjectEnrollment> findByProject_ProjectIdAndStatusAndDeletedAtIsNull(
        UUID projectId, String status);
    
    boolean existsByStudentIdAndProject_ProjectIdAndDeletedAtIsNull(
        UUID studentId, UUID projectId);
    
    // Changed from findByIdAndDeletedAtIsNull to findByEnrollmentIdAndDeletedAtIsNull
    Optional<ProjectEnrollment> findByEnrollmentIdAndDeletedAtIsNull(UUID enrollmentId);
}