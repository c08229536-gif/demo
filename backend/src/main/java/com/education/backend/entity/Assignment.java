package com.education.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "assignment")
public class Assignment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String title;

    private String description;
    
    @Column(name = "course_id")
    private Integer courseId;

    private LocalDateTime deadline;

    @Column(name = "create_time")
    private LocalDateTime createTime;

    @Transient
    private String status; // 待提交, 已提交, 已批改

    @Transient
    private Integer score;

    @Transient
    private String feedback;

    @Transient
    private Integer pendingCount; 
}