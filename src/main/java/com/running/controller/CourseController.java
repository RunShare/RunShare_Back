package com.running.controller;

import com.running.dto.GpxFileDto;
import com.running.service.GpxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

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

    @GetMapping("/{courseId}/file")
    public ResponseEntity<String> getGpxFile(@PathVariable Long courseId) {
        try {
            String gpxContent = gpxService.getGpxFileContent(courseId);
            return ResponseEntity.ok(gpxContent);
        } catch (Exception e) {
            e.printStackTrace(); //로그추가
            return ResponseEntity.notFound().build();
        }
    }
}