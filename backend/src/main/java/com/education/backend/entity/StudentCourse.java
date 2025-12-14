package com.education.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "student_course")
public class StudentCourse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // 谁选的？
    @Column(name = "user_id")
    private Integer userId;

    // 选了啥？
    @Column(name = "course_id")
    private Integer courseId;

    // 啥时候选的？
    @Column(name = "enroll_time")
    private LocalDateTime enrollTime;
}