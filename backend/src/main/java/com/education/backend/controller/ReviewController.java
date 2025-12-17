package com.education.backend.controller;

import com.education.backend.entity.CourseReview;
import com.education.backend.entity.User;
import com.education.backend.repository.CourseReviewRepository;
import com.education.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/review")
public class ReviewController {

    @Autowired
    private CourseReviewRepository reviewRepository;
    @Autowired
    private UserRepository userRepository;

    // 1. 获取某课程的所有评价
    @GetMapping("/course/{courseId}")
    public List<CourseReview> getReviews(@PathVariable Integer courseId) {
        List<CourseReview> reviews = reviewRepository.findByCourseIdOrderByCreateTimeDesc(courseId);
        // 填充学生信息
        reviews.forEach(r -> {
            userRepository.findById(r.getStudentId()).ifPresent(u -> {
                r.setStudentName(u.getRealName());
                r.setAvatar(u.getAvatar());
            });
        });
        return reviews;
    }

    // 2. 提交评价
    @PostMapping("/add")
    public String addReview(@RequestBody CourseReview review) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username).orElseThrow();

        // 检查是否评价过
        if (reviewRepository.existsByStudentIdAndCourseId(user.getUserId(), review.getCourseId())) {
            throw new RuntimeException("您已经评价过这门课了");
        }

        review.setStudentId(user.getUserId());
        reviewRepository.save(review);
        return "评价成功！";
    }
}