package com.running.repository;

import com.running.entity.GpxFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface GpxFileRepository extends JpaRepository<GpxFile, Long> {

    @Query("SELECT g FROM GpxFile g WHERE g.locationCode = :locationCode AND g.level = :level")
    Page<GpxFile> findByLocationCodeAndLevel(
            @Param("locationCode") String locationCode,
            @Param("level") String level,
            Pageable pageable
    );

    List<GpxFile> findByLocationCodeAndLevel(String locationCode, String level);
}