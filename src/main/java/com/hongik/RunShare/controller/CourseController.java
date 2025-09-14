/*
  '코스 추가, 수정, 삭제'의 컨트롤러
* */

package com.hongik.RunShare.controller;

import com.hongik.RunShare.domain.Course;
import com.hongik.RunShare.dto.AddCourseRequest;
import com.hongik.RunShare.dto.CourseResponse;
import com.hongik.RunShare.dto.UpdateCourseRequest;
import com.hongik.RunShare.service.CourseService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@CrossOrigin(origins = "http://localhost:3000") //당장은 react 3000번
public class CourseController {

    private final CourseService courseService;

    @PostMapping("/api/courses")
    public ResponseEntity<Course> addCourse(@RequestBody AddCourseRequest request){
        Course savedCourse = courseService.save(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(savedCourse);
    }

    @DeleteMapping("/api/courses/{id}")
    public ResponseEntity<Void> deleteCourse(@PathVariable long id){
        courseService.delete(id);

        return ResponseEntity.ok()
                .build();
    }

    @PutMapping("/api/courses/{id}")
    public ResponseEntity<Course> updateCourse(@RequestBody UpdateCourseRequest request, @PathVariable long id){
        Course updatedCourse = courseService.update(id, request);

        return ResponseEntity.ok()
                .body(updatedCourse);
    }

    @GetMapping("/api/courses") //조회
    public ResponseEntity<List<CourseResponse>> getAllCourses() {
        List<CourseResponse> courses = courseService.findAll()
                .stream()
                .map(CourseResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok().body(courses);
    }

    @GetMapping("/api/courses/{id}")
    public ResponseEntity<CourseResponse> getCourse(@PathVariable long id) {
        Course course = courseService.findById(id);
        return ResponseEntity.ok().body(new CourseResponse(course));
    }
}
