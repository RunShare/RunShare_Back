package com.running.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CooperTestRequest {
    private Long userId;
    private Double distance;  // 12분간 달린 거리 (미터)
    private Integer age;
    private String gender;  // "MALE" 또는 "FEMALE"
}