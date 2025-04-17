package com.example.ProjectService.DTO;

import lombok.*;
import java.util.UUID;



import java.util.UUID;

public class ProjectEnrollmentDto {
    private UUID projectId;
    private String status;

 
    public UUID getProjectId() {
        return projectId;
    }

    public void setProjectId(UUID projectId) {
        this.projectId = projectId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}