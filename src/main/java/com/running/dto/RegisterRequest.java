package com.running.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    @NotBlank(message = "아이디를 입력해주세요")
    @Size(min = 4, max = 10, message = "아이디는 4~10자리여야 합니다")
    private String username;

    @NotBlank(message = "비밀번호를 입력해주세요")
    @Size(min = 4, max = 10, message = "비밀번호는 4~10자리여야 합니다")
    private String password;

    @NotNull(message = "나이를 입력해주세요")
    @Min(value = 1, message = "나이는 1 이상이어야 합니다")
    @Max(value = 99, message = "나이는 99 이하여야 합니다")
    private Integer age;

    @NotNull(message = "몸무게를 입력해주세요")
    @Min(value = 1, message = "몸무게는 1 이상이어야 합니다")
    @Max(value = 200, message = "몸무게는 200 이하여야 합니다")
    private Double weight;

    @NotNull(message = "키를 입력해주세요")
    @Min(value = 1, message = "키는 1 이상이어야 합니다")
    @Max(value = 300, message = "키는 300 이하여야 합니다")
    private Double height;

    @NotBlank(message = "성별을 선택해주세요")
    private String gender;  // "MALE" 또는 "FEMALE"
}