package com.education.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "homework_submission")
public class HomeworkSubmission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "assignment_id")
    private Integer assignmentId;

    @Column(name = "student_id")
    private Integer studentId;

    private String content; // 作业内容

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "submit_time")
    private LocalDateTime submitTime;

    private Integer score;

    // 临时字段用于前端打分输入，不存数据库
    @Transient
    private Integer tempScore;
}