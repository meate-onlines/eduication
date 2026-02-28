package com.eduication.language.controller;

import com.eduication.language.dto.ApiResponse;
import com.eduication.language.entity.Course;
import com.eduication.language.entity.CourseResource;
import com.eduication.language.entity.UserAccount;
import com.eduication.language.enums.AccessLevel;
import com.eduication.language.enums.ResourceType;
import com.eduication.language.service.CourseService;
import com.eduication.language.service.CurrentUserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseService courseService;
    private final CurrentUserService currentUserService;

    public CourseController(CourseService courseService,
                            CurrentUserService currentUserService) {
        this.courseService = courseService;
        this.currentUserService = currentUserService;
    }

    @GetMapping
    public ApiResponse<List<Course>> list() {
        UserAccount user = currentUserService.requireCurrentUser();
        return ApiResponse.ok("查询成功", courseService.listCoursesForUser(user));
    }

    @PostMapping
    public ApiResponse<Course> createCourse(@Valid @RequestBody CreateCourseRequest request) {
        Course saved = courseService.createCourse(
                request.title(), request.language(), request.level(), request.description(), request.accessLevel());
        return ApiResponse.ok("课程创建成功", saved);
    }

    @PostMapping("/{courseId}/resources")
    public ApiResponse<CourseResource> createResource(@PathVariable Long courseId,
                                                      @Valid @RequestBody CreateCourseResourceRequest request) {
        CourseResource saved = courseService.createResource(courseId,
                request.title(),
                request.resourceType(),
                request.resourceUrl(),
                request.accessLevel(),
                request.description());
        return ApiResponse.ok("资源创建成功", saved);
    }

    @GetMapping("/{courseId}/resources")
    public ApiResponse<List<CourseResource>> listResources(@PathVariable Long courseId) {
        UserAccount user = currentUserService.requireCurrentUser();
        return ApiResponse.ok("查询成功", courseService.listResources(courseId, user));
    }

    public record CreateCourseRequest(
            @NotBlank(message = "课程标题不能为空")
            @Size(max = 120, message = "课程标题不能超过120字")
            String title,
            @NotBlank(message = "语种不能为空")
            String language,
            @NotBlank(message = "等级不能为空")
            String level,
            @NotBlank(message = "课程描述不能为空")
            String description,
            @NotNull(message = "资源权限不能为空")
            AccessLevel accessLevel
    ) {
    }

    public record CreateCourseResourceRequest(
            @NotBlank(message = "资源标题不能为空")
            String title,
            @NotNull(message = "资源类型不能为空")
            ResourceType resourceType,
            @NotBlank(message = "资源地址不能为空")
            String resourceUrl,
            @NotNull(message = "资源权限不能为空")
            AccessLevel accessLevel,
            String description
    ) {
    }
}
