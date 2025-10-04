package com.running.service;

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

        RunnerProfile saved = runnerProfileRepository.save(profile);

        return RunnerProfileDto.builder()
                .id(saved.getId())
                .level(saved.getLevel())
                .age(saved.getAge())
                .weight(saved.getWeight())
                .height(saved.getHeight())
                .build();
    }
}