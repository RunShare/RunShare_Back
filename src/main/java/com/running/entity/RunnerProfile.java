package com.running.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "runner_profile")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RunnerProfile {

    @Id
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    private User user;

    @Column(nullable = false)
    private String level;  // BEGINNER, INTERMEDIATE, ADVANCED

    private Integer age;

    private Double weight;

    private Double height;
}