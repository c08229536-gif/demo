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

    // 状态：不存数据库，临时计算
    @Transient
    private String status = "待提交"; 

    // 👇 新增：分数
    // 同样不存数据库，只在查列表时临时用来展示
    @Transient
    private Integer score;

    private String description;
    
    @Column(name = "course_id")
    private Integer courseId;

    private LocalDateTime deadline;
}