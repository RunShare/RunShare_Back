/*
  코스를 처음 생성할 때 정보를 받는 DTO 객체
 */

package com.hongik.RunShare.dto;

import com.hongik.RunShare.domain.Course;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class AddCourseRequest {
    private String name;
    private String content;

    public Course toEntity(){
        return Course.builder()
                .name(name)
                .content(content)
                .build();
    }
}
