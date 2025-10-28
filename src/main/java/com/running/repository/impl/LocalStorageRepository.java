package com.running.repository.impl;

import com.running.repository.GpxStorageRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
//@Repository
public class LocalStorageRepository implements GpxStorageRepository {

    @Value("${storage.storage-path}")
    private String basePath;  // application.yml gpx.storage-path 경로

    @Override
    public String save(Long userId, MultipartFile file) {
        try {
            // 사용자별 폴더 생성: basePath/userId/
            Path userDir = Paths.get(basePath, String.valueOf(userId));
            if (!Files.exists(userDir)) {
                Files.createDirectories(userDir);
            }

            // 파일명 생성 (중복 방지용)
            String originalFilename = file.getOriginalFilename();
            String filename = (originalFilename != null) ? originalFilename : "gpx_" + System.currentTimeMillis() + ".gpx";

            Path filePath = userDir.resolve(filename);

            // 파일 저장
            file.transferTo(filePath.toFile());
            log.info("GPX 파일 저장 완료: {}", filePath);

            // 상대경로를 반환하거나 절대경로 반환도 가능 (여기서는 상대경로 반환)
            return userDir.relativize(filePath).toString();
        } catch (IOException e) {
            throw new RuntimeException("GPX 파일 저장 실패", e);
        }
    }

    @Override
    public String update(Long userId, Long gpxId, MultipartFile file) {
        // update는 기존 파일 삭제 후 새 파일 저장하는 방식으로 구현할 수 있음
        // 실무에서는 gpxId를 이용해 기존 파일 경로를 DB에서 가져온 뒤 삭제 후 저장
        // 여기서는 단순히 save 재사용
        return save(userId, file);
    }

    @Override
    public String read(Long userId, Long gpxId, String filePath) {
        // 1. fullPath 확인 후 로그 출력
        String fullPath = basePath + "/" + userId + "/" + filePath;
        System.out.println("Trying to read: " + fullPath);

        // 2. 파일 내용 읽고 String으로 반환
        try {
            return Files.readString(Paths.get(basePath, String.valueOf(userId), filePath));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(Long userId, String filePath) {
        Path path = Paths.get(basePath, String.valueOf(userId), filePath);
        try {
            Files.deleteIfExists(path);
            log.info("GPX 파일 삭제 완료: {}", path);
        } catch (IOException e) {
            throw new RuntimeException("GPX 파일 삭제 실패: " + filePath, e);
        }
    }
}
