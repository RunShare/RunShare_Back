package com.running.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "gpx_file")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GpxFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long gpxId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Double startLat;

    @Column(nullable = false)
    private Double startLon;

    @Column(nullable = false)
    private String locationCode;  // 행정구역 코드 (예: 11 = 서울)

    @Column(nullable = false)
    private Double distance;  // 총 거리 (km)

    @Column(nullable = false)
    private Double altitude;  // 총 고도 변화량 (m)

    @Column(nullable = false)
    private String level;  // BEGINNER, INTERMEDIATE, ADVANCED

    @Column(nullable = false)
    private String filePath;  // GPX 파일 경로
}