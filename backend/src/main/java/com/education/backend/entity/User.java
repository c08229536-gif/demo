package com.education.backend.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "sys_user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userId;

    @Column(unique = true, nullable = false)
    private String username;

    // 👈 核心修复：JsonProperty 确保密码不返回给前端，但 JPA 内部操作（如日志记录）时密码依然存在，防止报错
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(nullable = false)
    private String password;

    private String realName;
    private String role;
    private String avatar;
    private String phone;
    private String email;
    
    @Column(name = "student_no")
    private String studentNo;

    @Column(nullable = false)
    private BigDecimal balance = new BigDecimal("1000.00");

    @Column(name = "create_time")
    private LocalDateTime createTime; 

    @PrePersist
    public void prePersist() {
        this.createTime = LocalDateTime.now();
    }

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "sys_user_role",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();
}