package com.education.backend.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;

@Data
@Entity
@Table(name = "exam_result")
public class ExamResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "exam_id")
    private Integer examId;

    @Column(name = "student_id")
    private Integer studentId;

    @Column(name = "score")
    private Integer score;

    @Column(name = "submit_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date submitTime;

    // 存储学生的答案（JSON字符串）
    @Column(name = "answers", columnDefinition = "TEXT")
    private String answers;

    // 0: 进行中, 1: 已提交
    @Column(name = "status")
    private Integer status = 0;

    @Column(name = "start_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date startTime;

    @Column(name = "switch_count")
    private Integer switchCount = 0;
}
