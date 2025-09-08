package com.hongik.RunShare.repository;

import com.hongik.RunShare.domain.Course;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, Long>
{
}
