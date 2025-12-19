package com.education.backend.entity;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "sys_log")
public class SysLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String operation;
    private String method;
    @Column(columnDefinition = "TEXT")
    private String params;
    private String ip;
    private LocalDateTime createTime;
}