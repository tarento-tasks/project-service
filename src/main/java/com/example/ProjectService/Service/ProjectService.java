package com.example.ProjectService.Service;

import com.example.ProjectService.DTO.ApiResponse;
import com.example.ProjectService.DTO.ProjectDTO;
import com.example.ProjectService.Exception.ResourceNotFoundException;
import com.example.ProjectService.Model.Project;
import com.example.ProjectService.Model.UserRef;
import com.example.ProjectService.Repository.ProjectRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

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
        dto.setMentorId(project.getMentor().getUserId());
        return dto;
    }

    @Transactional
    public ApiResponse<List<ProjectDTO>> getProjects(Optional<UUID> projectId) {
        if (projectId.isPresent()) {
            return projectRepository.findByProjectIdAndDeletedAtIsNull(projectId.get())
                    .map(project -> new ApiResponse<>(HttpStatus.OK.value(), "Project retrieved successfully", List.of(convertToDTO(project))))
                    .orElseThrow(() -> new ResourceNotFoundException("Project not found or has been deleted"));
        }

        List<ProjectDTO> projectList = projectRepository.findByDeletedAtIsNull().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return new ApiResponse<>(HttpStatus.OK.value(), "Projects retrieved successfully", projectList);
    }

    @Transactional
    public ProjectDTO createProject(ProjectDTO projectDTO) {
        // Title uniqueness check
        if (projectRepository.findByTitleIgnoreCase(projectDTO.getTitle()).isPresent()) {
            throw new IllegalArgumentException("Project title already exists. Choose a different name.");
        }

        validateDates(projectDTO);

        Project project = new Project();
        populateProjectFromDTO(project, projectDTO);
        return convertToDTO(projectRepository.save(project));
    }

    @Transactional
    public ProjectDTO updateProject(UUID id, ProjectDTO projectDTO) {
        Project existingProject = projectRepository.findById(id)
                .filter(p -> p.getDeletedAt() == null)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found or has been deleted"));

        validateDates(projectDTO);

        // Check for title conflict with other projects
        Optional<Project> existingWithTitle = projectRepository.findByTitleIgnoreCase(projectDTO.getTitle());
        if (existingWithTitle.isPresent() && !existingWithTitle.get().getProjectId().equals(id)) {
            throw new IllegalArgumentException("Another project with the same title already exists.");
        }

        populateProjectFromDTO(existingProject, projectDTO);
        return convertToDTO(projectRepository.save(existingProject));
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

    private void validateDates(ProjectDTO dto) {
        LocalDate today = LocalDate.now();
        if (dto.getLastDate().isBefore(today)) {
            throw new IllegalArgumentException("Last date cannot be before the current date.");
        }
        if (dto.getDueDate().isBefore(today)) {
            throw new IllegalArgumentException("Due date cannot be before the current date.");
        }
        if (!dto.getLastDate().isBefore(dto.getDueDate())) {
            throw new IllegalArgumentException("Last date must be before due date.");
        }
    }

    private void populateProjectFromDTO(Project project, ProjectDTO dto) {
        project.setTitle(dto.getTitle());
        project.setObjective(dto.getObjective());
        project.setDescription(dto.getDescription());
        project.setDueDate(dto.getDueDate());
        project.setCriteria(dto.getCriteria());
        project.setRepo(dto.getRepo());
        project.setLastDate(dto.getLastDate());
        project.setOpenStatus(dto.isOpenStatus());

        UserRef mentorRef = new UserRef();
        mentorRef.setUserId(dto.getMentorId());
        project.setMentor(mentorRef);
    }
}
