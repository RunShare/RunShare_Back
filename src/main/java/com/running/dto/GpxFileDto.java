package com.running.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GpxFileDto {
    private Long gpxId;
    private String name;
    private Double startLat;
    private Double startLon;
    private String locationCode;
    private Double distance;
    private Double altitude;
    private String level;
    private Double distanceFromUser;  // 사용자로부터의 거리 (계산된 값)
}