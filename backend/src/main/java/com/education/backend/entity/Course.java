package com.education.backend.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "course")
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer courseId;

    // 对应数据库的 title 列
    private String title;
    
    // 对应数据库的 teacher 列 (这里先简化为字符串，存老师名字)
    private String teacher;
    
    // 对应数据库的 cover 列
    private String cover;
    
    // 对应数据库的 description 列
    private String description;

    // 0:待审核, 1:已发布, 2:已驳回
    private Integer status = 0;

    @Transient
    private Double rating; // 平均评分

    private String category;
    
    @Column(name = "teacher_id") 
    private Integer teacherId;

    
}