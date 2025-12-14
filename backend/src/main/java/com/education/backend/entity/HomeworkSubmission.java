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

    @Column(name = "submit_time")
    private LocalDateTime submitTime;

    private Integer score;
}