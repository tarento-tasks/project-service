package com.example.ProjectService.DTO;

import java.util.UUID;

public class SkillMappingRequest {
    private UUID userId;
    private UUID skillId;

    public SkillMappingRequest() {}

    public SkillMappingRequest(UUID userId, UUID skillId) {
        this.userId = userId;
        this.skillId = skillId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public UUID getSkillId() {
        return skillId;
    }

    public void setSkillId(UUID skillId) {
        this.skillId = skillId;
    }
}
