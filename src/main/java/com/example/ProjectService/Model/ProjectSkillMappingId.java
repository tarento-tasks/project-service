package com.example.ProjectService.Model;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProjectSkillMappingId implements Serializable {
    private UUID projectId;
    private UUID skillId;
}
