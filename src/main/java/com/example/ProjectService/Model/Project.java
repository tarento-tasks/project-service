package com.example.ProjectService.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID projectId;
    @ElementCollection
    @CollectionTable(name = "project_skills", joinColumns = @JoinColumn(name = "project_id"))
    @Column(name = "skill_id")
    private Set<UUID> requiredSkillIds = new HashSet<>();
    private String title;
    private String objective;
    private String description;
    private LocalDate dueDate;
    private String criteria;
    private String repo;
    private LocalDate lastDate;
    private boolean openStatus;
    private LocalDateTime deletedAt;

    // ✅ Add this for mentor (UserRef)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mentor_id", referencedColumnName = "userId")
    private UserRef mentor;
}
