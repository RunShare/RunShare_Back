package com.running.repository;

import com.running.entity.RunnerProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RunnerProfileRepository extends JpaRepository<RunnerProfile, Long> {
}