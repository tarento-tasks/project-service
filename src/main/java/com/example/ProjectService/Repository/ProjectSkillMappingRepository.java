// package com.example.ProjectService.Repository;

// import com.example.ProjectService.Model.ProjectSkillMapping;
// import com.example.ProjectService.Model.ProjectSkillMappingId;
// import org.springframework.data.jpa.repository.JpaRepository;

// import java.util.List;
// import java.util.UUID;

// public interface ProjectSkillMappingRepository extends JpaRepository<ProjectSkillMapping, ProjectSkillMappingId> {

//     List<ProjectSkillMapping> findByProject_ProjectId(UUID projectId);

//     List<ProjectSkillMapping> findById_SkillId(UUID skillId);

//     List<ProjectSkillMapping> findById_SkillIdIn(List<UUID> skillIds);

//     boolean existsById_ProjectIdAndId_SkillId(UUID projectId, UUID skillId);
// }
