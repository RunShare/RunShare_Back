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
    private String level;  // 5step으로 더   다양하게 만듬
    private Integer age;
    private Double weight;
    private Double height;
    private String gender; //male female 쿠퍼테스트위해서 이번에 추가함
}