/*
 GET Method로 조회한 다음 DB에서 정보를 가져올 때 정보를 담는 DTO 객체
* */

package com.hongik.RunShare.dto;

import com.hongik.RunShare.domain.Course;
import lombok.Getter;

@Getter
public class CourseResponse {
    private final Long id;
    private final String name;
    private final String content;
    private final Long id;

    public CourseResponse(Course course){
        this.id = course.getId();
        this.name = course.getName();
        this.content = course.getContent();
    }
}
