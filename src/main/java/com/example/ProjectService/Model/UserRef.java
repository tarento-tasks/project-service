package com.example.ProjectService.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
public class UserRef {

    @Id
    private UUID userId;

}
