package com.running.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "locations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long locId;

    @Column(nullable = false)
    private String name;  // 행정구역 이름 (예: 서울특별시)

    @Column(nullable = false, unique = true)
    private String locationCode;  // 행정구역 코드 (예: 11)
}