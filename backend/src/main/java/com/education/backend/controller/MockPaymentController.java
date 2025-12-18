package com.education.backend.controller;

import com.education.backend.entity.Course;
import com.education.backend.entity.Order;
import com.education.backend.entity.StudentCourse;
import com.education.backend.entity.User;
import com.education.backend.repository.CourseRepository;
import com.education.backend.repository.OrderRepository;
import com.education.backend.repository.StudentCourseRepository;
import com.education.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/payment")
public class MockPaymentController {

    @Autowired private UserRepository userRepository;
    @Autowired private CourseRepository courseRepository;
    @Autowired private StudentCourseRepository studentCourseRepository;
    @Autowired private OrderRepository orderRepository;

    @PostMapping("/buy")
    @Transactional // 开启事务，保证扣款和选课同时成功或失败
    public String buyCourse(@RequestBody BuyRequest request) {
        // 1. 获取用户和课程
        User user = userRepository.findById(request.getUserId()).orElseThrow(() -> new RuntimeException("用户不存在"));
        Course course = courseRepository.findById(request.getCourseId()).orElseThrow(() -> new RuntimeException("课程不存在"));

        // 2. 检查是否已购买
        if (studentCourseRepository.existsByUserIdAndCourseId(user.getUserId(), course.getCourseId())) {
            throw new RuntimeException("您已拥有该课程，无需重复购买");
        }

        // 3. 检查余额
        if (user.getBalance().compareTo(course.getPrice()) < 0) {
            throw new RuntimeException("余额不足，请先充值！当前余额: ￥" + user.getBalance());
        }

        // 4. 扣款
        user.setBalance(user.getBalance().subtract(course.getPrice()));
        userRepository.save(user);

        // 5. 生成订单记录
        Order order = new Order();
        order.setOrderNo(UUID.randomUUID().toString());
        order.setUserId(user.getUserId());
        order.setCourseId(course.getCourseId());
        order.setAmount(course.getPrice());
        order.setCreateTime(LocalDateTime.now());
        orderRepository.save(order);

        // 6. 核心步骤：给学生开通课程 (写入 student_course 表)
        StudentCourse sc = new StudentCourse();
        sc.setUserId(user.getUserId());
        sc.setCourseId(course.getCourseId());
        sc.setEnrollTime(LocalDateTime.now());
        studentCourseRepository.save(sc);

        return "支付成功！剩余余额：￥" + user.getBalance();
    }
    
    // 简单的充值接口（用于测试）
    @PostMapping("/recharge")
    public String recharge(@RequestParam Integer userId, @RequestParam BigDecimal amount) {
        User user = userRepository.findById(userId).orElseThrow();
        user.setBalance(user.getBalance().add(amount));
        userRepository.save(user);
        return "充值成功，当前余额：" + user.getBalance();
    }

    // 内部类用于接收JSON参数
    @lombok.Data
    static class BuyRequest {
        private Integer userId;
        private Integer courseId;
    }
}
