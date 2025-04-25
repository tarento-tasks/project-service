// package com.example.ProjectService.Service;

// import com.example.ProjectService.Client.UserServiceClient;
// import com.example.ProjectService.DTO.*;
// import com.example.ProjectService.Model.*;
// import com.example.ProjectService.Repository.*;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.HttpStatus;
// import org.springframework.stereotype.Service;

// import java.util.*;
// import java.util.stream.Collectors;

// @Service
// public class ProjectSkillMappingService {

//     @Autowired
//     private ProjectSkillMappingRepository projectSkillMappingRepository;

//     @Autowired
//     private ProjectRepository projectRepository;

//     @Autowired
//     private ProjectEnrollmentRepository projectEnrollmentRepository;

//     @Autowired
//     private UserServiceClient userServiceClient;

//     public ApiResponse<ProjectSkillMappingDTO> addSkillToProject(UUID projectId, UUID skillId) {
//         Project project = projectRepository.findById(projectId)
//                 .orElseThrow(() -> new RuntimeException("Project not found"));

//         SkillDTO skillDTO = userServiceClient.getSkillById(skillId);
//         if (skillDTO == null) {
//             throw new RuntimeException("Skill not found");
//         }

//         ProjectSkillMapping projectSkillMapping = ProjectSkillMapping.builder()
//         .id(new ProjectSkillMappingId(projectId, skillId))
//         .project(project)
//         .build();


//         projectSkillMappingRepository.save(projectSkillMapping);

//         ProjectSkillMappingDTO responseDTO = new ProjectSkillMappingDTO(projectId, skillId);
//         return new ApiResponse<>(HttpStatus.CREATED.value(), "Skill mapped to project successfully", responseDTO);
//     }

//     public ApiResponse<?> getProjectSkillMappings(UUID studentId, UUID projectId, UUID skillId) {
//         if (studentId != null) {
//             return getRecommendedProjectsForStudent(studentId);
//         } else if (projectId != null) {
//             return getSkillsForProject(projectId);
//         } else if (skillId != null) {
//             return getProjectsForSkill(skillId);
//         } else {
//             return new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), "Invalid request. Provide studentId, projectId, or skillId.", null);
//         }
//     }

//     public ApiResponse<List<ProjectDTO>> getRecommendedProjectsForStudent(UUID studentId) {
//         List<SkillMappingRequest> studentSkills = userServiceClient.getSkillMappingsByUserId(studentId);
//         List<UUID> skillIds = studentSkills.stream()
//                 .map(SkillMappingRequest::getSkillId)
//                 .toList();

//         if (skillIds.isEmpty()) {
//             return new ApiResponse<>(HttpStatus.OK.value(), "No skills found for student. No recommendations available.", Collections.emptyList());
//         }

//         List<ProjectSkillMapping> matchedProjects = projectSkillMappingRepository.findById_SkillIdIn(skillIds);

//         List<ProjectDTO> projectDTOs = matchedProjects.stream()
//                 .map(ProjectSkillMapping::getProject)
//                 .distinct()
//                 .map(project -> {
//                     ProjectDTO dto = new ProjectDTO();
//                     dto.setProjectId(project.getProjectId());
//                     dto.setTitle(project.getTitle());
//                     dto.setObjective(project.getObjective());
//                     dto.setDescription(project.getDescription());
//                     dto.setDueDate(project.getDueDate());
//                     dto.setCriteria(project.getCriteria());
//                     dto.setRepo(project.getRepo());
//                     dto.setLastDate(project.getLastDate());
//                     dto.setOpenStatus(project.isOpenStatus());
//                     dto.setMentorId(project.getMentor().getUserId());
//                     return dto;
//                 })
//                 .collect(Collectors.toList());

//         return new ApiResponse<>(HttpStatus.OK.value(), "Recommended projects", projectDTOs);
//     }

//     public ApiResponse<List<SkillDTO>> getSkillsByProjectId(UUID projectId) {
//         List<ProjectSkillMapping> mappings = projectSkillMappingRepository.findByProject_ProjectId(projectId);

//         if (mappings.isEmpty()) {
//             return new ApiResponse<>(HttpStatus.OK.value(), "No skills found for this project", Collections.emptyList());
//         }

//         List<SkillDTO> skills = mappings.stream()
//         .map(mapping -> {
//             UUID skillId = mapping.getId().getSkillId();
//             SkillDTO skillDTO = userServiceClient.getSkillById(skillId);
//             return skillDTO;
//         })
        
//                 .collect(Collectors.toList());

//         return new ApiResponse<>(HttpStatus.OK.value(), "Skills for project retrieved successfully", skills);
//     }

//     public ApiResponse<List<UserDTO>> getRecommendedStudentsForProject(UUID projectId) {
//         List<SkillMappingRequest> skillMappings = userServiceClient.getSkillMappingsByProjectId(projectId);
//         List<UUID> skillIds = skillMappings.stream()
//                 .map(SkillMappingRequest::getSkillId)
//                 .distinct()
//                 .toList();

//         List<UUID> recommendedStudentIds = userServiceClient.getRecommendedStudents(skillIds, projectId);

//         List<UserDTO> recommendedStudents = recommendedStudentIds.stream()
//                 .map(userServiceClient::getUserById)
//                 .toList();

//                 return new ApiResponse<>(HttpStatus.OK.value(), "Recommended students fetched successfully.", recommendedStudents);

//     }

//     private ApiResponse<List<SkillDTO>> getSkillsForProject(UUID projectId) {
//         List<ProjectSkillMapping> mappings = projectSkillMappingRepository.findByProject_ProjectId(projectId);
    
//         List<SkillDTO> skills = mappings.stream()
//             .map(mapping -> userServiceClient.getSkillById(mapping.getId().getSkillId()))
//             .filter(Objects::nonNull)
//             .collect(Collectors.toList());
    
//         return new ApiResponse<>(HttpStatus.OK.value(), "Skills required for project", skills);
//     }
    

//     private ApiResponse<List<ProjectSkillMappingDTO>> getProjectsForSkill(UUID skillId) {
//         List<ProjectSkillMappingDTO> mappings = projectSkillMappingRepository.findById_SkillId(skillId)
//                 .stream()
//                 .map(mapping -> new ProjectSkillMappingDTO(
//     mapping.getProject().getProjectId(),
//     mapping.getId().getSkillId()  
// ))

//                 .collect(Collectors.toList());

//         return new ApiResponse<>(HttpStatus.OK.value(), "Projects requiring this skill", mappings);
//     }
// }
