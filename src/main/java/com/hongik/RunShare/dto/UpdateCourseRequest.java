/*
  업데이트할 데이터를 담은 DTO 객체
  @NoArgsConstructor : 빈칸으로 내용을 변경할 수도 있으니 자동 생성
  @ALlArgsConstructor : 전 매개변수가 새로운 내용으로 바뀔 수 있음
 */

package com.hongik.RunShare.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UpdateCourseRequest {
    private String name;
    private String content;
}
