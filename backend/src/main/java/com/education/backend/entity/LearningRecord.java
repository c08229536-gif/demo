package com.education.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "learning_record")
public class LearningRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer recordId;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "course_id")
    private Integer courseId;

    @Column(name = "chapter_id")
    private Integer chapterId;

    // 0: 未学完, 1: 已学完
    private Integer status;

    @Column(name = "finish_time")
    private LocalDateTime finishTime;
}