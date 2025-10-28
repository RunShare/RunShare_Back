package com.running.controller;

import com.running.dto.CooperTestRequest;
import com.running.dto.RunnerProfileDto;
import com.running.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/{userId}")
    public ResponseEntity<RunnerProfileDto> getRunnerProfile(@PathVariable Long userId) {
        RunnerProfileDto profile = userService.getRunnerProfile(userId);
        if (profile == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(profile);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<RunnerProfileDto> updateRunnerProfile(
            @PathVariable Long userId,
            @RequestBody RunnerProfileDto dto) {
        try {
            RunnerProfileDto updated = userService.updateRunnerProfile(userId, dto);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            e.printStackTrace(); //로그추가
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{userId}/cooper-test")
    public ResponseEntity<?> saveCooperTestResult(
            @PathVariable Long userId,
            @RequestBody CooperTestRequest request) {
        try {
            String newLevel = userService.calculateAndUpdateLevel(request);
            return ResponseEntity.ok(Map.of(
                    "level", newLevel,
                    "message", "쿠퍼 테스트 결과가 저장되었습니다"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}