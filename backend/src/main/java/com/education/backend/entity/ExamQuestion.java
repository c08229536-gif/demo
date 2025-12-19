package com.education.backend.entity;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "exam_question")
public class ExamQuestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer examId;
    @Column(columnDefinition = "TEXT")
    private String content;
    private String type;
    @Column(columnDefinition = "TEXT")
    private String options; // 存储JSON字符串
    private String answer;
    private Integer score;
}