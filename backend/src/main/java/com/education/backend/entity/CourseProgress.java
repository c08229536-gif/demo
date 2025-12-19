package com.education.backend.entity;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "course_progress")
public class CourseProgress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    private Long courseId;
    private Long chapterId;
    private Integer progressPercent;
    private Integer isFinished; // 对应 TINYINT(1)
    @Column(insertable = false, updatable = false)
    private LocalDateTime updateTime;
}
