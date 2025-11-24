package com.running.controller;

import com.running.dto.GpxFileDto;
import com.running.entity.GpxFile;
import com.running.service.GpxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
public class CourseController{

    @Autowired
    private GpxService gpxService;

    @GetMapping
    public ResponseEntity<Page<GpxFileDto>> getCourses(
            @RequestParam Long userId,
            @RequestParam double lat,
            @RequestParam double lng,
            @RequestParam(defaultValue = "distance") String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        try {
            if (sort.equals("distance")) {
                // 거리순 정렬은 별도 처리 필요
                List<GpxFileDto> courses = gpxService.getRecommendedCoursesSortedByDistance(userId, lat, lng);

                // 페이지네이션 처리
                int start = page * size;
                int end = Math.min(start + size, courses.size());

                if (start >= courses.size()) {
                    return ResponseEntity.ok(Page.empty());
                }

                List<GpxFileDto> pagedCourses = courses.subList(start, end);
                return ResponseEntity.ok(new org.springframework.data.domain.PageImpl<>(
                        pagedCourses,
                        org.springframework.data.domain.PageRequest.of(page, size),
                        courses.size()
                ));
            } else {
                // 이름순 정렬
                Page<GpxFileDto> courses = gpxService.getRecommendedCourses(userId, lat, lng, sort, page, size);
                return ResponseEntity.ok(courses);
            }
        } catch (Exception e) {
            e.printStackTrace(); //로그추가
            return ResponseEntity.badRequest().build();
        }
    }

    /*
    * gpx 파일 읽어오기
    * 파일 내용을 String으로 변환해서 데이터를 전송한다.
    * */
    @GetMapping("/{courseId}/file")
    public ResponseEntity<String> getGpxFile(
            @RequestParam Long userId,
            @PathVariable Long courseId) {
        try {
            String gpxContent = gpxService.getGpxFile(userId, courseId);
            return ResponseEntity.ok(gpxContent);
        } catch (Exception e) {
            e.printStackTrace(); //로그추가
            return ResponseEntity.notFound().build();
        }
    }

    /*
     * gpx 파일 업로드
     * consumes : 파일 form 요청만 처리함.
     * */
    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<GpxFile> saveGpx(
            @RequestParam Long userId,
            @RequestParam("file") MultipartFile file) {

        try {
            // 서비스는 파일 유효성 검증(확장자/Content-Type/크기), 객체 스토리지 업로드, DB 저장 처리
            GpxFile savedGpxFile = gpxService.saveGpxFile(userId, file);
            return ResponseEntity.ok(savedGpxFile);
        } catch (IllegalArgumentException bad) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    /*
     * gpx 파일 수정
     * consumes : 파일 form 요청만 처리함.
     * id : id 해당되는 gpx 파일 변경
     * 생성과 수정 로직이 같아서 수정은 제외
     * */
    /*
    @PatchMapping(path = "/{id}/file", consumes = "multipart/form-data")
    public ResponseEntity<MultipartFile> updateGpxFile(
            @RequestParam Long gpxId,
            @RequestParam Long userId,
            @RequestParam("file") MultipartFile file) {
        try {
            MultipartFile updatedGpxFile = gpxService.updateGpxFile(gpxId, userId, file);
            return ResponseEntity.ok(updatedGpxFile);
        } catch (IllegalArgumentException notFoundOrBad) {
            // 존재하지 않거나 잘못된 파일 등
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }*/

    /*
     * gpx 파일 삭제
     * id : id 해당되는 gpx 파일 삭제
     * */
    @DeleteMapping("/{courseId}")
    public ResponseEntity<Void> deleteGpxFile(
            @RequestParam Long userId,
            @PathVariable Long courseId) {
        try {
            gpxService.deleteGpxFile(userId, courseId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException notFound) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}