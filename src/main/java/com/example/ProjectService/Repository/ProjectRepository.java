package com.example.ProjectService.Repository;

import com.example.ProjectService.Model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, UUID> {
    List<Project> findByDeletedAtIsNull();
    Optional<Project> findByTitleIgnoreCase(String title);
    
    Optional<Project> findByProjectIdAndDeletedAtIsNull(UUID projectId);
}