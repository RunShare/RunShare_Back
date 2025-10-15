package com.running.repository;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;

public interface GpxStorageRepository {
    String save(Long userId, MultipartFile file);

    String update(Long userId, Long gpxId, MultipartFile file);

    File read(String filePath);

    void delete(Long userId, String filePath);
}
