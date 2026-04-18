package com.education.backend.service.impl;

import com.education.backend.entity.Course;
import com.education.backend.entity.Order;
import com.education.backend.entity.StudentCourse;
import com.education.backend.entity.User;
import com.education.backend.repository.CourseRepository;
import com.education.backend.repository.OrderRepository;
import com.education.backend.repository.StudentCourseRepository;
import com.education.backend.repository.UserRepository;
import com.education.backend.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private StudentCourseRepository studentCourseRepository;
    @Autowired
    private OrderRepository orderRepository;

    @Override
    @Transactional
    public String buyCourse(Integer userId, Integer courseId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("用户不存在"));
        Course course = courseRepository.findById(courseId).orElseThrow(() -> new RuntimeException("课程不存在"));

        if (studentCourseRepository.existsByUserIdAndCourseId(userId, courseId)) {
            throw new RuntimeException("您已拥有该课程，无需重复购买");
        }

        if (user.getBalance().compareTo(course.getPrice()) < 0) {
            throw new RuntimeException("余额不足，请先充值！当前余额: ￥" + user.getBalance());
        }

        user.setBalance(user.getBalance().subtract(course.getPrice()));
        userRepository.save(user);

        Order order = new Order();
        order.setOrderNo(UUID.randomUUID().toString());
        order.setUserId(userId);
        order.setCourseId(courseId);
        order.setAmount(course.getPrice());
        order.setCreateTime(LocalDateTime.now());
        orderRepository.save(order);

        StudentCourse sc = new StudentCourse();
        sc.setUserId(userId);
        sc.setCourseId(courseId);
        sc.setEnrollTime(LocalDateTime.now());
        studentCourseRepository.save(sc);

        return "支付成功！剩余余额：￥" + user.getBalance();
    }
}
