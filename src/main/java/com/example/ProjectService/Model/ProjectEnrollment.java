
// package com.example.ProjectService.Model;
// import jakarta.persistence.*;
// import lombok.*;
// import java.time.LocalDateTime;
// import java.util.UUID;

// @Data
// @Entity
// @NoArgsConstructor
// @AllArgsConstructor
// @Table(name = "project_enrollment")
// public class ProjectEnrollment {

//     @Id
//     @GeneratedValue(strategy = GenerationType.AUTO)
//     private UUID enrollmentId;
     
//     @ManyToOne
//     @JoinColumn(name = "student_id", referencedColumnName = "userId")
//     private UserRef student;
    

//     @ManyToOne
//     @JoinColumn(name = "project_id", nullable = false)
//     private Project project;

//     @Column(nullable = false)
//     private String status = "PENDING"; // PENDING, APPROVED, REJECTED

//     @Column(nullable = false, updatable = false)
//     private LocalDateTime createdAt = LocalDateTime.now();

//     private LocalDateTime modifiedAt;
//     private LocalDateTime deletedAt;
// }
