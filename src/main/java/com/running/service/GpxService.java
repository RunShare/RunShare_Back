package com.running.service;

import com.running.dto.GpxFileDto;
import com.running.entity.GpxFile;
import com.running.entity.RunnerProfile;
import com.running.repository.GpxFileRepository;
import com.running.repository.GpxStorageRepository;
import com.running.repository.RunnerProfileRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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

    @Autowired
    private GpxStorageRepository gpxStorageRepository;

    @Autowired
    private GpxAnalysisService gpxAnalysisService;

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

    public String getGpxFileContent(Long userId, Long gpxId) throws IOException {
        GpxFile gpxFile = gpxFileRepository.findById(gpxId)
                .orElseThrow(() -> new RuntimeException("GPX file not found"));

        String fullPath = gpxStoragePath + "/" + userId + "/" +gpxFile.getFilePath();
        System.out.println("Trying to read: " + fullPath); // 로그 추가
        return Files.readString(Paths.get(gpxStoragePath, String.valueOf(userId), gpxFile.getFilePath()));
    }

    // 전형진: gpx 파일 저장: gpx 파일 자체 저장
    @Transactional
    public GpxFile saveGpxFile(Long userId, MultipartFile file) throws IOException {
        // 0. 파일 오류 검사
        if (file.isEmpty()) {
            throw new IllegalArgumentException("업로드된 파일이 비어 있습니다.");
        }

        String originalFileName = file.getOriginalFilename();
        byte[] fileBytes = file.getBytes();

        // 1. 파일 스토리지에 저장 후 filepath 주입
        // S3 마이그레이션 경우 key 값 주입
        String filepath = gpxStorageRepository.save(userId, file);

        // 2. 파일 분석 후 gpx_file 테이블에 메타데이터 저장
        GpxFile gpxFile = gpxAnalysisService.analyzeAndSave(fileBytes, filepath, originalFileName);
        return gpxFile;
    }

    /*
    // gpx 파일 수정
    @Transactional
    public MultipartFile updateGpxFile(Long userId, Long gpxId, MultipartFile file){
        // 1. gpxStorageRepo S3 저장 후 key 반환
        String savedKey = gpxStorageRepository.save(userId, file);

        // 2. gpx 파일 분석

        // 3. GpxFile 엔티티에 저장
    }
    */

    // 전형진: gpx 파일 삭제
    // 자신이 만든 파일만 삭제하는 로직 추가 필요
    @Transactional
    public void deleteGpxFile(Long userId, Long gpxId){
        // 1. 삭제하고자 하는 gpx 파일 조회
        GpxFile gpxFile = gpxFileRepository.findById(gpxId)
                .orElseThrow(() -> new IllegalArgumentException("GPX file not found"));

        // 2. gpx 메타데이터 삭제
        gpxFileRepository.delete(gpxFile);

        // 3. gpx 원본 파일 삭제
        gpxStorageRepository.delete(userId, gpxFile.getFilePath());
    }

}