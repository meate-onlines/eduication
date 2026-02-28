package com.eduication.language.service;

import com.eduication.language.entity.Course;
import com.eduication.language.entity.CourseResource;
import com.eduication.language.entity.UserAccount;
import com.eduication.language.enums.AccessLevel;
import com.eduication.language.enums.ResourceType;
import com.eduication.language.exception.BusinessException;
import com.eduication.language.repository.CourseRepository;
import com.eduication.language.repository.CourseResourceRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseService {

    private final CourseRepository courseRepository;
    private final CourseResourceRepository courseResourceRepository;

    public CourseService(CourseRepository courseRepository,
                         CourseResourceRepository courseResourceRepository) {
        this.courseRepository = courseRepository;
        this.courseResourceRepository = courseResourceRepository;
    }

    public List<Course> listCoursesForUser(UserAccount user) {
        return courseRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .filter(course -> canAccess(user, course.getAccessLevel()))
                .toList();
    }

    public Course createCourse(String title, String language, String level, String description, AccessLevel accessLevel) {
        Course course = new Course();
        course.setTitle(title);
        course.setLanguage(language);
        course.setLevel(level);
        course.setDescription(description);
        course.setAccessLevel(accessLevel);
        return courseRepository.save(course);
    }

    public CourseResource createResource(Long courseId,
                                         String title,
                                         ResourceType resourceType,
                                         String resourceUrl,
                                         AccessLevel accessLevel,
                                         String description) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new BusinessException("课程不存在"));
        CourseResource resource = new CourseResource();
        resource.setCourse(course);
        resource.setTitle(title);
        resource.setResourceType(resourceType);
        resource.setResourceUrl(resourceUrl);
        resource.setAccessLevel(accessLevel);
        resource.setDescription(description);
        return courseResourceRepository.save(resource);
    }

    public List<CourseResource> listResources(Long courseId, UserAccount user) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new BusinessException("课程不存在"));
        if (!canAccess(user, course.getAccessLevel())) {
            throw new BusinessException("该课程为会员专享，请先订阅");
        }
        return courseResourceRepository.findByCourseIdOrderByCreatedAtDesc(courseId)
                .stream()
                .filter(resource -> canAccess(user, resource.getAccessLevel()))
                .toList();
    }

    private boolean canAccess(UserAccount user, AccessLevel accessLevel) {
        if (accessLevel == AccessLevel.NORMAL) {
            return true;
        }
        return user != null && user.isVip();
    }
}
