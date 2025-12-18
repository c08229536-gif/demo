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
@RequestMapping("/payment")
public class MockPaymentController {

    @Autowired private UserRepository userRepository;
    @Autowired private CourseRepository courseRepository;
    @Autowired private StudentCourseRepository studentCourseRepository;
    @Autowired private OrderRepository orderRepository;

    @PostMapping("/buy")
    @Transactional
    public String buyCourse(@RequestBody BuyRequest request) {
        User user = userRepository.findById(request.getUserId()).orElseThrow(() -> new RuntimeException("用户不存在"));
        Course course = courseRepository.findById(request.getCourseId()).orElseThrow(() -> new RuntimeException("课程不存在"));

        if (studentCourseRepository.existsByUserIdAndCourseId(user.getUserId(), course.getCourseId())) {
            throw new RuntimeException("您已拥有该课程，无需重复购买");
        }

        if (user.getBalance().compareTo(course.getPrice()) < 0) {
            throw new RuntimeException("余额不足，请先充值！当前余额: ￥" + user.getBalance());
        }

        user.setBalance(user.getBalance().subtract(course.getPrice()));
        userRepository.save(user);

        Order order = new Order();
        order.setOrderNo(UUID.randomUUID().toString());
        order.setUserId(user.getUserId());
        order.setCourseId(course.getCourseId());
        order.setAmount(course.getPrice());
        order.setCreateTime(LocalDateTime.now());
        orderRepository.save(order);

        StudentCourse sc = new StudentCourse();
        sc.setUserId(user.getUserId());
        sc.setCourseId(course.getCourseId());
        sc.setEnrollTime(LocalDateTime.now());
        studentCourseRepository.save(sc);

        return "支付成功！剩余余额：￥" + user.getBalance();
    }

    // 👇👇👇【重点修改】改成了接收 JSON 对象 👇👇👇
    @PostMapping("/recharge")
    public String recharge(@RequestBody RechargeRequest request) {
        User user = userRepository.findById(request.getUserId()).orElseThrow(() -> new RuntimeException("用户不存在"));
        
        // 执行充值
        user.setBalance(user.getBalance().add(request.getAmount()));
        userRepository.save(user);
        
        return "充值成功，当前余额：" + user.getBalance();
    }

    // 用于接收购买参数
    @lombok.Data
    static class BuyRequest {
        private Integer userId;
        private Integer courseId;
    }

    // 👇👇👇【新增】用于接收充值参数的内部类 👇👇👇
    @lombok.Data
    static class RechargeRequest {
        private Integer userId;
        private BigDecimal amount;
    }
}