package com.education.backend.entity;

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

    private Integer userId; // [cite: 73]

    @Column(unique = true, nullable = false)

    private String username; // [cite: 74]



    @Column(nullable = false)

    private String password; // [cite: 75]



    private String realName;

    private String role; // [cite: 76]

    private String avatar;   // [cite: 77]
    private String phone;
    private String email;
    
    @Column(name = "student_no")
    private String studentNo;

    @Column(nullable = false)
    private BigDecimal balance = new BigDecimal("1000.00"); // 初始赠送1000元用于测试

    @Column(name = "create_time")
    private LocalDateTime createTime; 



    @PrePersist

    public void prePersist() {

        this.createTime = LocalDateTime.now();

    }



    // 多对多关系映射：用户-角色 [cite: 108]

    @ManyToMany(fetch = FetchType.EAGER)

    @JoinTable(

        name = "sys_user_role",

        joinColumns = @JoinColumn(name = "user_id"),

        inverseJoinColumns = @JoinColumn(name = "role_id")

    )

    private Set<Role> roles = new HashSet<>();

}