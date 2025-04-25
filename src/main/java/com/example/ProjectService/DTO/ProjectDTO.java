package com.example.ProjectService.DTO;

import lombok.Data;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;



@Data
public class ProjectDTO {
    private UUID projectId;
    private String title;
    private String objective;
    private String description;
    private LocalDate dueDate;
    private String criteria;
    private String repo;
    private LocalDate lastDate = LocalDate.now();
    private boolean openStatus;
    private UUID mentorId;
    private List<UUID> skillIds;
}