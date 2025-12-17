package com.education.backend.controller;

import com.education.backend.entity.CourseResource;
import com.education.backend.repository.CourseResourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/resource")
public class CourseResourceController {

    @Autowired
    private CourseResourceRepository resourceRepository;

    // 1. 获取某课程的所有资料
    @GetMapping("/course/{courseId}")
    public List<CourseResource> getResources(@PathVariable Integer courseId) {
        return resourceRepository.findByCourseIdOrderByCreateTimeDesc(courseId);
    }

    // 2. 添加资料 (老师用)
    @PostMapping("/add")
    public String addResource(@RequestBody CourseResource resource) {
        if (resource.getTitle() == null || resource.getUrl() == null) {
            throw new RuntimeException("标题和链接不能为空");
        }
        resourceRepository.save(resource);
        return "资料添加成功！";
    }

    // 3. 删除资料 (老师用)
    @DeleteMapping("/{id}")
    public String deleteResource(@PathVariable Integer id) {
        resourceRepository.deleteById(id);
        return "删除成功";
    }
}
