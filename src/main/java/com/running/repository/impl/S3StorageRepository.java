package com.running.repository.impl;

import com.running.repository.GpxStorageRepository;
import io.awspring.cloud.s3.S3Template;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class S3StorageRepository implements GpxStorageRepository {

    private final S3Template s3Template;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucketName; // application.yml에서 주입해도 됨

    @Override
    public String save(Long userId, MultipartFile file) {
        try (InputStream input = file.getInputStream()) {
            String key = "user-" + userId + "/gpx/" + file.getOriginalFilename(); // S3 파일 경로: user-{userId}/gpx/test.gpx
            s3Template.upload(bucketName, key, input);
            return key; // DB에는 key(S3 경로)만 저장
        } catch (IOException e) {
            throw new RuntimeException("S3 파일 업로드 실패", e);
        }
    }

    @Override
    public String update(Long userId, Long gpxId, MultipartFile file) {
        try (InputStream input = file.getInputStream()) {
            String key = "user-" + userId + "/gpx/" + gpxId + "-" + file.getOriginalFilename();
            s3Template.upload(bucketName, key, input); // S3는 overwrite 방식
            return key;
        } catch (IOException e) {
            throw new RuntimeException("S3 파일 수정 실패", e);
        }
    }

    @Override
    public String read(Long userId, Long gpxId, String filePath) {
        // 예: filePath = "user-1/gpx/run1.gpx"
        // 1. S3에서 객체로 표적 파일 다운로드
        var s3Object = s3Template.download(bucketName, filePath);

        System.out.println("filePath = " + filePath);

        // 2. 파일을 읽고 String으로 반환
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(s3Object.getInputStream(), StandardCharsets.UTF_8))) {
            return reader.lines().collect(Collectors.joining("\n"));
        } catch (IOException e) {
            throw new RuntimeException("S3 파일 읽기 실패: " + filePath, e);
        }
    }

    @Override
    public void delete(Long userId, String filePath) {
        try {
            s3Template.deleteObject(bucketName, filePath);
        } catch (Exception e) {
            throw new RuntimeException("S3 파일 삭제 실패", e);
        }
    }
}
