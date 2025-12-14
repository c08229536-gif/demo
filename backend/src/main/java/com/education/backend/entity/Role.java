package com.education.backend.entity;



import jakarta.persistence.*;

import lombok.Data;



@Data

@Entity

@Table(name = "sys_role")

public class Role {

   

    @Id

    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Integer roleId; // [cite: 81]



    @Column(unique = true, nullable = false)

    private String roleName; // [cite: 82]



    private String description; // [cite: 83]

}