// src/main/java/com/running/service/GpxAnalysisService.java
package com.running.service;

import com.running.entity.GpxFile;
import com.running.repository.GpxFileRepository;
import io.jenetics.jpx.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.file.Path;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GpxAnalysisService {

    private final GpxFileRepository gpxFileRepository;
    private final LocationService locationService;

    @Transactional
    public GpxFile analyzeAndSave(byte[] fileBytes, String filepath, String filename) {
        try (InputStream in = new ByteArrayInputStream(fileBytes)) {
            // 1) GPX 파싱 (InputStream 사용)
            GPX gpx = GPX.Reader.of(GPX.Reader.Mode.LENIENT).read(in); // GPX 3.0 권장 API Reader 사용

            // 2) 트랙 포인트 수집
            List<WayPoint> points = gpx.tracks()
                    .flatMap(Track::segments)
                    .flatMap(TrackSegment::points)
                    .toList();

            if (points.isEmpty()) {
                throw new IllegalArgumentException("GPX에 유효한 트랙 포인트가 없습니다.");
            }

            // 3) 시작점/거리/고도변화 계산
            WayPoint start = points.get(0);
            double startLat = start.getLatitude().doubleValue();
            double startLon = start.getLongitude().doubleValue();

            double totalMeters = 0.0;
            double totalAltitudeChange = 0.0;

            Double prevLat = null, prevLon = null, prevEle = null;
            for (WayPoint p : points) {
                double lat = p.getLatitude().doubleValue();
                double lon = p.getLongitude().doubleValue();
                Double ele = p.getElevation().map(Length::doubleValue).orElse(null);

                if (prevLat != null && prevLon != null) {
                    totalMeters += haversineMeters(prevLat, prevLon, lat, lon);
                }
                if (prevEle != null && ele != null) {
                    totalAltitudeChange += Math.abs(ele - prevEle);
                }

                prevLat = lat; prevLon = lon; prevEle = ele;
            }

            double distanceKm = round(totalMeters / 1000.0, 3);
            double altitudeChangeM = round(totalAltitudeChange, 1);

            // 4) 위치 코드
            String locationCode = locationService.getLocationCodeFromCoordinates(startLat, startLon);

            // 5) 난이도 계산
            String level = classifyLevel(distanceKm, altitudeChangeM);

            // 6) 엔티티 저장
            GpxFile entity = GpxFile.builder()
                    .name(filename != null ? filename : inferName(gpx))
                    .startLat(startLat)
                    .startLon(startLon)
                    .locationCode(locationCode)
                    .distance(distanceKm)
                    .altitude(altitudeChangeM)
                    .level(level)
                    .filePath(filepath)
                    .build();

            return gpxFileRepository.save(entity);

        } catch (Exception e) {
            throw new RuntimeException("GPX 분석 실패: " + e.getMessage(), e);
        }
    }

    private static double haversineMeters(double lat1, double lon1, double lat2, double lon2) {
        // WGS84
        double R = 6371_000.0; // meters
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat/2)*Math.sin(dLat/2)
                + Math.cos(Math.toRadians(lat1))*Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon/2)*Math.sin(dLon/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return R * c;
    }

    private static String classifyLevel(double distanceKm, double altitudeM) {
        // 예시 기준: 거리와 고도변화 가중
        double score = distanceKm + (altitudeM / 200.0); // 200m 고도 = 1km 난이도 가중
        if (score < 8)  return "BEGINNER";
        if (score < 16) return "INTERMEDIATE";
        return "ADVANCED";
    }

    private static String inferName(GPX gpx) {
        // <name> 태그가 있으면 사용
        return gpx.getMetadata()
                .flatMap(Metadata::getName)
                .orElse("GPX-" + UUID.randomUUID().toString().substring(0, 8));
    }

    private static double round(double v, int scale) {
        double p = Math.pow(10, scale);
        return Math.round(v * p) / p;
    }
}
