package com.example.ProjectService.Controller;

import com.example.ProjectService.DTO.ApiResponse;
import com.example.ProjectService.DTO.ProjectDTO;
import com.example.ProjectService.Service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api/projects")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProjectDTO>>> getProjects(@RequestParam(required = false) UUID id) {
        return ResponseEntity.ok(projectService.getProjects(Optional.ofNullable(id)));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ProjectDTO>> createOrUpdateProject(
            @RequestBody ProjectDTO projectDTO, @RequestParam(required = false) UUID id) {
        try {
            ProjectDTO savedProject = projectService.saveOrUpdateProject(Optional.ofNullable(id), projectDTO);
            return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Project saved successfully", savedProject));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), e.getMessage(), null));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(HttpStatus.NOT_FOUND.value(), e.getMessage(), null));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteProject(@PathVariable UUID id) {
        if (projectService.deleteProject(id)) {
            return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Project deleted successfully", null));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse<>(HttpStatus.NOT_FOUND.value(), "Project not found or already deleted", null));
    }
}