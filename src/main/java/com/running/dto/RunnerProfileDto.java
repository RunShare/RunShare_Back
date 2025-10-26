package com.running.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RunnerProfileDto {
    private Long id;
    private String level;
    private Integer age;
    private Double weight;
    private Double height;
    private String gender;
}