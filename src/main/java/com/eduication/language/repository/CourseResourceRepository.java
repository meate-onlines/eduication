package com.eduication.language.repository;

import com.eduication.language.entity.CourseResource;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseResourceRepository extends JpaRepository<CourseResource, Long> {
    List<CourseResource> findByCourseIdOrderByCreatedAtDesc(Long courseId);
}
