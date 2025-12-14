package com.education.backend.entity;



import jakarta.persistence.*;

import lombok.Data;

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



    @Column(name = "create_time")

    private LocalDateTime createTime; // [cite: 79]



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