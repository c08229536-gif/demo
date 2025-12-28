package com.education.backend.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.*; // 如果是旧版Spring Boot请使用 javax.persistence
import java.util.Date;

@Data
@Entity
@Table(name = "exam")
public class Exam {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "title", nullable = false)
    private String title;

    // 关联的课程ID
    @Column(name = "course_id")
    private Integer courseId;

    // 发布试卷的老师ID
    @Column(name = "teacher_id")
    private Integer teacherId;

    // 考试描述
    @Column(name = "description")
    private String description;

    // 考试时长（分钟）
    @Column(name = "duration")
    private Integer duration;

    // 考试总分
    @Column(name = "total_score")
    private Integer totalScore;

    // 考试开始时间 - 重点修复：规范化时间格式
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "start_time")
    private Date startTime;

    // 考试结束时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "end_time")
    private Date endTime;

    // 状态：0-未发布/草稿, 1-已发布
    @Column(name = "state")
    private Integer state;
    
    // 创建时间
    @Column(name = "create_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
    
    @PrePersist
    public void prePersist() {
        if (this.createTime == null) {
            this.createTime = new Date();
        }
        if (this.state == null) {
            this.state = 0; // 默认为草稿
        }
    }
}