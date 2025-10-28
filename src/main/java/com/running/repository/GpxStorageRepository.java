package com.running.repository;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;

public interface GpxStorageRepository {
    String save(Long userId, MultipartFile file);

    String update(Long userId, Long gpxId, MultipartFile file);

    String read(Long userId, Long gpxId, String filePath);

    void delete(Long userId, String filePath);
}
