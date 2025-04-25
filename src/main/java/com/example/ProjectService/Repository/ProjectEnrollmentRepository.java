// package com.example.ProjectService.Repository;

// import com.example.ProjectService.Model.ProjectEnrollment;
// import org.springframework.data.jpa.repository.JpaRepository;
// import org.springframework.stereotype.Repository;

// import java.util.List;
// import java.util.Optional;
// import java.util.UUID;

// @Repository
// public interface ProjectEnrollmentRepository extends JpaRepository<ProjectEnrollment, UUID> {
    

//     List<ProjectEnrollment> findByDeletedAtIsNull();

//     List<ProjectEnrollment> findByStudentIdAndDeletedAtIsNull(UUID studentId);

//     List<ProjectEnrollment> findByStudentIdAndStatusAndDeletedAtIsNull(UUID studentId, String status);

//     List<ProjectEnrollment> findByProject_ProjectIdAndStatusAndDeletedAtIsNull(UUID projectId, String status);

//     Optional<ProjectEnrollment> findByIdAndDeletedAtIsNull(UUID enrollmentId);


//     List<ProjectEnrollment> findByProject_Mentor_EmailAndProject_DeletedAtIsNullAndDeletedAtIsNull(String mentorEmail);
//     List<ProjectEnrollment> findByStudent_EmailAndProject_DeletedAtIsNullAndDeletedAtIsNull(String studentEmail);
//     List<ProjectEnrollment> findByProject_DeletedAtIsNullAndDeletedAtIsNull();

//     Optional<ProjectEnrollment> findByEnrollmentIdAndProject_DeletedAtIsNullAndDeletedAtIsNull(UUID enrollmentId);

//     Optional<ProjectEnrollment> findByStudent_UserIdAndProject_ProjectIdAndDeletedAtIsNull(UUID studentId, UUID projectId);

//     List<ProjectEnrollment> findByProject_ProjectIdAndStatus(UUID projectId, String status);

//     boolean existsByStudent_UserIdAndProject_ProjectIdAndDeletedAtIsNull(UUID studentId, UUID projectId);



//     List<ProjectEnrollment> findByStudent_UserIdAndDeletedAtIsNullAndProject_DeletedAtIsNull(UUID studentId);
    
    
 
//     List<ProjectEnrollment> findByStudent_UserIdAndStatusAndDeletedAtIsNullAndProject_DeletedAtIsNull(
//     UUID studentId,
//     String status);
 
  
// }