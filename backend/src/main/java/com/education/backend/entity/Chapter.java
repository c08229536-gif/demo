package com.education.backend.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "chapter")
public class Chapter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer chapterId;

    @Column(name = "course_id")
    private Integer courseId;

    private String title;
    private String description;
    
    @Column(name = "video_url")
    private String videoUrl;
    
    @Column(name = "sort_order")
    private Integer sortOrder;
}