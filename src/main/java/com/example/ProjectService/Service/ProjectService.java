package com.example.ProjectService.Service;

import com.example.ProjectService.DTO.ApiResponse;
import com.example.ProjectService.DTO.ProjectDTO;
import com.example.ProjectService.Exception.ResourceNotFoundException;
import com.example.ProjectService.Model.Project;
import com.example.ProjectService.Exception.BadRequestException;
import com.example.ProjectService.Repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.context.annotation.Bean;

@Service
public class ProjectService {
    @Autowired
private HttpServletRequest request;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${user.service.url}")
    private String userServiceUrl;

    

    private ProjectDTO convertToDTO(Project project) {
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
        dto.setMentorId(project.getMentorId());
        return dto;
    }

    @Transactional
    public ApiResponse<List<ProjectDTO>> getProjects(Optional<UUID> projectId) {
        if (projectId.isPresent()) {
            return projectRepository.findByProjectIdAndDeletedAtIsNull(projectId.get())
                    .map(project -> new ApiResponse<>(HttpStatus.OK.value(),
                            "Project retrieved successfully",
                            List.of(convertToDTO(project))))
                    .orElseThrow(() -> new ResourceNotFoundException("Project not found or has been deleted"));
        }
        List<ProjectDTO> projectList = projectRepository.findByDeletedAtIsNull().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return new ApiResponse<>(HttpStatus.OK.value(), "Projects retrieved successfully", projectList);
    }

    @Transactional
public ProjectDTO saveOrUpdateProject(Optional<UUID> projectId, ProjectDTO projectDTO) {
    LocalDateTime currentDateTime = LocalDateTime.now();
    LocalDate currentDate = currentDateTime.toLocalDate();

    // Validate dates
    if (projectDTO.getLastDate().isBefore(currentDate)) {
        throw new IllegalArgumentException("Last date cannot be before the current date.");
    }
    if (projectDTO.getDueDate().isBefore(currentDate)) {
        throw new IllegalArgumentException("Due date cannot be before the current date.");
    }
    if (!projectDTO.getLastDate().isBefore(projectDTO.getDueDate())) {
        throw new IllegalArgumentException("Last date must be before due date");
    }

    // Verify mentor ID exists
    UUID mentorId = projectDTO.getMentorId();
    if (mentorId == null) {
        throw new BadRequestException("Mentor ID is required");
    }

    try {
        HttpHeaders headers = new HttpHeaders();
        String token = request.getHeader("Authorization");
        if (token != null) {
            headers.set("Authorization", token);
        }
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        
        ResponseEntity<String> response = restTemplate.exchange(
            userServiceUrl + "?userId=" + mentorId,
            HttpMethod.GET,
            entity,
            String.class
        );

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new ResourceNotFoundException("Mentor verification failed with status: " + response.getStatusCode());
        }
    } catch (HttpClientErrorException e) {
        if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
            throw new ResourceNotFoundException("Mentor not found");
        } else if (e.getStatusCode() == HttpStatus.FORBIDDEN) {
            throw new BadRequestException("Access denied while verifying mentor. Please check service authentication.");
        }
        throw new BadRequestException("Failed to verify mentor: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
    } catch (Exception e) {
        throw new BadRequestException("Failed to verify mentor: " + e.getMessage());
    }

    // Get existing project if updating
    Project project = projectId.flatMap(projectRepository::findById)
            .filter(p -> p.getDeletedAt() == null)
            .orElse(new Project());

    // Only check for duplicate title if this is a new project or the title is being changed
    if (project.getProjectId() == null || !project.getTitle().equals(projectDTO.getTitle())) {
        if (projectRepository.existsByTitleAndRepoAndDeletedAtIsNull(projectDTO.getTitle(), projectDTO.getRepo())) {
            throw new IllegalArgumentException("A project with the same title and repository already exists");
        }
    }

    project.setTitle(projectDTO.getTitle());
    project.setObjective(projectDTO.getObjective());
    project.setDescription(projectDTO.getDescription());
    project.setDueDate(projectDTO.getDueDate());
    project.setCriteria(projectDTO.getCriteria());
    project.setRepo(projectDTO.getRepo());
    project.setLastDate(projectDTO.getLastDate());
    project.setOpenStatus(projectDTO.isOpenStatus());
    project.setMentorId(projectDTO.getMentorId());

    if (project.getProjectId() == null) {
        project.setCreatedAt(currentDateTime);
    } else {
        project.setModifiedAt(currentDateTime);
    }

    return convertToDTO(projectRepository.save(project));
}
    @Transactional
    public boolean deleteProject(UUID id) {
        return projectRepository.findById(id)
                .filter(p -> p.getDeletedAt() == null)
                .map(p -> {
                    p.setDeletedAt(LocalDateTime.now());
                    projectRepository.save(p);
                    return true;
                }).orElse(false);
    }
}