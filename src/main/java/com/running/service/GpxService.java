package com.running.service;

import com.running.dto.GpxFileDto;
import com.running.entity.GpxFile;
import com.running.entity.RunnerProfile;
import com.running.repository.GpxFileRepository;
import com.running.repository.RunnerProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GpxService {

    @Autowired
    private GpxFileRepository gpxFileRepository;

    @Autowired
    private RunnerProfileRepository runnerProfileRepository;

    @Autowired
    private LocationService locationService;

    @Value("${gpx.storage-path}")
    private String gpxStoragePath;

    public Page<GpxFileDto> getRecommendedCourses(Long userId, double userLat, double userLon,
                                                  String sortBy, int page, int size) {
        // 사용자 레벨 조회
        RunnerProfile profile = runnerProfileRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Runner profile not found"));

        String userLevel = profile.getLevel();

        // 사용자 위치에서 location_code 조회
        String locationCode = locationService.getLocationCodeFromCoordinates(userLat, userLon);

        if (locationCode == null) {
            throw new RuntimeException("Location code not found for coordinates");
        }

        // 정렬 기준 설정
        Sort sort = sortBy.equals("name") ? Sort.by("name").ascending() : Sort.unsorted();
        Pageable pageable = PageRequest.of(page, size, sort);

        // DB에서 같은 지역, 같은 레벨의 코스 조회
        Page<GpxFile> gpxFiles = gpxFileRepository.findByLocationCodeAndLevel(locationCode, userLevel, pageable);

        // 거리순 정렬이면 사용자 위치로부터의 거리 계산
        return gpxFiles.map(gpx -> {
            double distance = locationService.calculateDistance(userLat, userLon, gpx.getStartLat(), gpx.getStartLon());

            return GpxFileDto.builder()
                    .gpxId(gpx.getGpxId())
                    .name(gpx.getName())
                    .startLat(gpx.getStartLat())
                    .startLon(gpx.getStartLon())
                    .locationCode(gpx.getLocationCode())
                    .distance(gpx.getDistance())
                    .altitude(gpx.getAltitude())
                    .level(gpx.getLevel())
                    .distanceFromUser(distance)
                    .build();
        });
    }

    public List<GpxFileDto> getRecommendedCoursesSortedByDistance(Long userId, double userLat, double userLon) {
        // 사용자 레벨 조회
        RunnerProfile profile = runnerProfileRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Runner profile not found"));

        String userLevel = profile.getLevel();

        // 사용자 위치에서 location_code 조회
        String locationCode = locationService.getLocationCodeFromCoordinates(userLat, userLon);

        if (locationCode == null) {
            throw new RuntimeException("Location code not found for coordinates");
        }

        // 모든 해당 코스 조회
        List<GpxFile> gpxFiles = gpxFileRepository.findByLocationCodeAndLevel(locationCode, userLevel);

        // 거리 계산 후 정렬
        return gpxFiles.stream()
                .map(gpx -> {
                    double distance = locationService.calculateDistance(userLat, userLon, gpx.getStartLat(), gpx.getStartLon());
                    return GpxFileDto.builder()
                            .gpxId(gpx.getGpxId())
                            .name(gpx.getName())
                            .startLat(gpx.getStartLat())
                            .startLon(gpx.getStartLon())
                            .locationCode(gpx.getLocationCode())
                            .distance(gpx.getDistance())
                            .altitude(gpx.getAltitude())
                            .level(gpx.getLevel())
                            .distanceFromUser(distance)
                            .build();
                })
                .sorted((a, b) -> Double.compare(a.getDistanceFromUser(), b.getDistanceFromUser()))
                .collect(Collectors.toList());
    }

    public String getGpxFileContent(Long gpxId) throws IOException {
        GpxFile gpxFile = gpxFileRepository.findById(gpxId)
                .orElseThrow(() -> new RuntimeException("GPX file not found"));

        String fullPath = gpxStoragePath + gpxFile.getFilePath();
        System.out.println("Trying to read: " + fullPath); // 로그 추가
        return Files.readString(Paths.get(fullPath));
    }
}