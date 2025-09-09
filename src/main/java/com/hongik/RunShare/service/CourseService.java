/*
  '코스 추가, 수정, 삭제'의 서비스 계층
  리포지토리 계층을 이용해 코스를 다루는 메소드를 만들고 서비스 계층이 사용할 수 있게 함.
* */

package com.hongik.RunShare.service;

import com.hongik.RunShare.domain.Course;
import com.hongik.RunShare.dto.AddCourseRequest;
import com.hongik.RunShare.dto.CourseResponse;
import com.hongik.RunShare.dto.UpdateCourseRequest;
import com.hongik.RunShare.repository.CourseRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class CourseService {
    private final CourseRepository courseRepository;

    // 코스 저장 리포지토리 메소드(POST)
    public Course save(AddCourseRequest request) {return courseRepository.save(request.toEntity());}

    public void delete(long id) { courseRepository.deleteById(id); }

    public List<Course> findAll(){
        return courseRepository.findAll();
    }

    public Course findById(long id){
        return courseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found: " + id));
    }

    @Transactional
    public Course update(long id, UpdateCourseRequest request){
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found: " + id));

        course.update(request.getName(), request.getContent());
        courseRepository.flush();
        return course;
    }
}
