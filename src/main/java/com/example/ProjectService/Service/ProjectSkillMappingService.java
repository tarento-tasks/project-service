package com.example.ProjectService.Service;

import com.example.ProjectService.DTO.ApiResponse;
import com.example.ProjectService.DTO.ProjectDTO;
import com.example.ProjectService.DTO.ProjectSkillMappingDTO;
import com.example.ProjectService.Model.Project;
import com.example.ProjectService.Model.ProjectEnrollment;
import com.example.ProjectService.Model.ProjectSkillMapping;
import com.example.ProjectService.Model.ProjectSkillMappingId;
import com.example.ProjectService.Repository.ProjectEnrollmentRepository;
import com.example.ProjectService.Repository.ProjectRepository;
import com.example.ProjectService.Repository.ProjectSkillMappingRepository;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDate;
import java.util.*;
import java.util.AbstractMap.SimpleEntry;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProjectSkillMappingService {

    private final ProjectSkillMappingRepository mappingRepo;
    private final ProjectRepository projectRepo;
    private final ProjectEnrollmentRepository enrollmentRepo;
    private final RestTemplate restTemplate;

    @Value("${user.service.url}")
    private String userServiceUrl;

    @Autowired
    public ProjectSkillMappingService(ProjectSkillMappingRepository mappingRepo,
                                    ProjectRepository projectRepo,
                                    ProjectEnrollmentRepository enrollmentRepo,
                                    RestTemplate restTemplate) {
        this.mappingRepo = mappingRepo;
        this.projectRepo = projectRepo;
        this.enrollmentRepo = enrollmentRepo;
        this.restTemplate = restTemplate;
    }

    public ApiResponse<ProjectSkillMappingDTO> addSkillToProject(UUID projectId, UUID skillId) {
        Project project = projectRepo.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        ProjectSkillMapping m = ProjectSkillMapping.builder()
                .id(new ProjectSkillMappingId(projectId, skillId))
                .project(project)
                .build();
        mappingRepo.save(m);
        return new ApiResponse<>(HttpStatus.CREATED.value(),
                "Skill mapped to project successfully",
                new ProjectSkillMappingDTO(projectId, skillId));
    }

    public ApiResponse<List<UUID>> getSkillsByProjectId(UUID projectId) {
        List<UUID> skills = mappingRepo.findByProject_ProjectId(projectId)
                .stream()
                .map(m -> m.getId().getSkillId())
                .collect(Collectors.toList());
        return new ApiResponse<>(HttpStatus.OK.value(), "Skills for project retrieved successfully", skills);
    }

    public ApiResponse<?> getProjectSkillMappings(UUID studentId, UUID projectId, UUID skillId) {
        if (studentId != null) {
            return getRecommendedProjectsForStudent(studentId);
        } else if (projectId != null) {
            return getSkillsForProject(projectId);
        } else if (skillId != null) {
            return getProjectsForSkill(skillId);
        } else {
            return new ApiResponse<>(HttpStatus.BAD_REQUEST.value(),
                    "Invalid request. Provide studentId, projectId, or skillId.", null);
        }
    }

    public ApiResponse<List<ProjectDTO>> getRecommendedProjectsForStudent(UUID studentId) {
        // Get student's skills from UserService
        List<UUID> skillIds = getStudentSkills(studentId);
        
        if (skillIds.isEmpty()) {
            return new ApiResponse<>(HttpStatus.OK.value(),
                    "No skills found for student. No recommendations available.", Collections.emptyList());
        }

        // Find projects that match these skills
        List<ProjectSkillMapping> matchedProjects = mappingRepo.findById_SkillIdIn(skillIds);

        List<ProjectDTO> projectDTOs = matchedProjects.stream()
                .map(ProjectSkillMapping::getProject)
                .distinct()
                .filter(project -> project.isOpenStatus() && 
                       (project.getLastDate() == null || !project.getLastDate().isBefore(LocalDate.now())))
                .map(this::toProjectDTO)
                .collect(Collectors.toList());

        return new ApiResponse<>(HttpStatus.OK.value(), "Recommended projects", projectDTOs);
    }

    public ApiResponse<List<UUID>> getRecommendedStudentsForProject(UUID projectId) {
        // Get required skill IDs for the project
        List<UUID> requiredSkillIds = mappingRepo.findByProject_ProjectId(projectId)
                .stream()
                .map(mapping -> mapping.getId().getSkillId())
                .collect(Collectors.toList());

        if (requiredSkillIds.isEmpty()) {
            return new ApiResponse<>(HttpStatus.OK.value(),
                    "No skills required for this project", Collections.emptyList());
        }
        List<ProjectEnrollment> enrollments = enrollmentRepo.findByProject_ProjectIdAndStatusAndDeletedAtIsNull(projectId, "PENDING");

        

        // Get student IDs from enrollments
        List<UUID> studentIds = enrollments.stream()
                .map(ProjectEnrollment::getStudentId)
                .collect(Collectors.toList());

        // Get recommended students with their match counts
        List<SimpleEntry<UUID, Long>> studentMatches = studentIds.stream()
                .map(studentId -> {
                    List<UUID> studentSkillIds = getStudentSkills(studentId);
                    long matchCount = studentSkillIds.stream()
                            .filter(requiredSkillIds::contains)
                            .count();
                    return new SimpleEntry<>(studentId, matchCount);
                })
                .filter(entry -> entry.getValue() > 0)
                .sorted((e1, e2) -> Long.compare(e2.getValue(), e1.getValue()))
                .limit(10)
                .collect(Collectors.toList());

        // Extract just the student IDs
        List<UUID> recommendedStudents = studentMatches.stream()
                .map(SimpleEntry::getKey)
                .collect(Collectors.toList());

        return new ApiResponse<>(HttpStatus.OK.value(),
                "Recommended student IDs", recommendedStudents);
    }

    // ─── Private Helper Methods ────────────────────────────────────────────────

    private List<UUID> getStudentSkills(UUID studentId) {
        String token = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest().getHeader("Authorization");
        HttpHeaders headers = new HttpHeaders();
        if (token != null && !token.startsWith("Bearer ")) {
            token = "Bearer " + token;
        }
        headers.set("Authorization", token);
        
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<JsonNode> response = restTemplate.exchange(
                    userServiceUrl + "/skill-mapping?userId=" + studentId,
                    HttpMethod.GET,
                    entity,
                    JsonNode.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                JsonNode skillsNode = response.getBody().path("response");
List<UUID> skillIds = new ArrayList<>();
skillsNode.forEach(skill -> skillIds.add(UUID.fromString(skill.path("skillId").asText())));
return skillIds;

            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch student skills from UserService", e);
        }
        return Collections.emptyList();
    }

    private ProjectDTO toProjectDTO(Project project) {
        ProjectDTO dto = new ProjectDTO();
        dto.setProjectId(project.getProjectId());
        dto.setTitle(project.getTitle());
        dto.setObjective(project.getObjective());
        dto.setDescription(project.getDescription());
        dto.setDueDate(project.getDueDate());
        dto.setCriteria(project.getCriteria());
        dto.setRepo(project.getRepo());
        dto.setLastDate(project.getLastDate());
        dto.setOpenStatus(project.isOpenStatus());
        return dto;
    }

    private ApiResponse<List<UUID>> getSkillsForProject(UUID projectId) {
        List<UUID> skills = mappingRepo.findByProject_ProjectId(projectId)
                .stream()
                .map(m -> m.getId().getSkillId())
                .collect(Collectors.toList());
        return new ApiResponse<>(HttpStatus.OK.value(), "Skills required for project", skills);
    }

    private ApiResponse<List<ProjectSkillMappingDTO>> getProjectsForSkill(UUID skillId) {
        List<ProjectSkillMappingDTO> dtos = mappingRepo.findById_SkillId(skillId)
                .stream()
                .map(m -> new ProjectSkillMappingDTO(m.getProject().getProjectId(), skillId))
                .collect(Collectors.toList());
        return new ApiResponse<>(HttpStatus.OK.value(), "Projects requiring this skill", dtos);
    }
}