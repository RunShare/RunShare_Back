package com.running.service;

import com.running.dto.CooperTestRequest;
import com.running.dto.RunnerProfileDto;
import com.running.entity.RunnerProfile;
import com.running.entity.User;
import com.running.repository.RunnerProfileRepository;
import com.running.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RunnerProfileRepository runnerProfileRepository;

    public RunnerProfileDto getRunnerProfile(Long userId) {
        RunnerProfile profile = runnerProfileRepository.findById(userId)
                .orElse(null);

        if (profile == null) {
            return null;
        }

        return RunnerProfileDto.builder()
                .id(profile.getId())
                .level(profile.getLevel())
                .age(profile.getAge())
                .weight(profile.getWeight())
                .height(profile.getHeight())
                .gender(profile.getGender())
                .build();
    }

    @Transactional
    public RunnerProfileDto updateRunnerProfile(Long userId, RunnerProfileDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        RunnerProfile profile = runnerProfileRepository.findById(userId)
                .orElse(RunnerProfile.builder()
                        .user(user)
                        .build());

        profile.setLevel(dto.getLevel());
        profile.setAge(dto.getAge());
        profile.setWeight(dto.getWeight());
        profile.setHeight(dto.getHeight());
        profile.setGender(dto.getGender());

        RunnerProfile saved = runnerProfileRepository.save(profile);

        return RunnerProfileDto.builder()
                .id(saved.getId())
                .level(saved.getLevel())
                .age(saved.getAge())
                .weight(saved.getWeight())
                .height(saved.getHeight())
                .gender(saved.getGender())
                .build();
    }

    @Transactional
    public String calculateAndUpdateLevel(CooperTestRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));

        RunnerProfile profile = user.getRunnerProfile();
        if (profile == null) {
            throw new IllegalArgumentException("프로필을 찾을 수 없습니다");
        }

        // 레벨 계산
        String newLevel = calculateLevel(
                request.getDistance(),
                request.getAge(),
                request.getGender()
        );

        // 레벨 업데이트
        profile.setLevel(newLevel);
        runnerProfileRepository.save(profile);

        return newLevel;
    }

    private String calculateLevel(Double distance, Integer age, String gender) {
        // 성별과 나이에 따른 기준값 설정
        int[][] maleCriteria = {
                {2700, 2400, 2200, 2100}, // 13-14
                {2800, 2500, 2300, 2200}, // 15-16
                {3000, 2700, 2500, 2300}, // 17-19
                {2800, 2400, 2200, 1600}, // 20-29
                {2700, 2300, 1900, 1500}, // 30-39
                {2500, 2100, 1700, 1400}, // 40-49
                {2400, 2000, 1600, 1300}  // 50+
        };

        int[][] femaleCriteria = {
                {2000, 1900, 1600, 1500}, // 13-14
                {2100, 2000, 1700, 1600}, // 15-16
                {2300, 2100, 1800, 1700}, // 17-20
                {2700, 2200, 1800, 1500}, // 20-29
                {2500, 2000, 1700, 1400}, // 30-39
                {2300, 1900, 1500, 1200}, // 40-49
                {2200, 1700, 1400, 1100}  // 50+
        };

        // 나이 그룹 결정
        int ageGroup;
        if (age <= 14) ageGroup = 0;
        else if (age <= 16) ageGroup = 1;
        else if (age <= 19 || (gender.equals("FEMALE") && age <= 20)) ageGroup = 2;
        else if (age <= 29) ageGroup = 3;
        else if (age <= 39) ageGroup = 4;
        else if (age <= 49) ageGroup = 5;
        else ageGroup = 6;

        // 기준값 선택
        int[] criteria = gender.equals("MALE") ? maleCriteria[ageGroup] : femaleCriteria[ageGroup];

        // 레벨 판정
        if (distance >= criteria[0]) return "ADVANCED";           // Excellent
        else if (distance >= criteria[1]) return "UPPER_INTERMEDIATE";  // Above Average
        else if (distance >= criteria[2]) return "INTERMEDIATE";        // Average
        else if (distance >= criteria[3]) return "LOWER_INTERMEDIATE";  // Below Average
        else return "BEGINNER";                                          // Poor
    }
}