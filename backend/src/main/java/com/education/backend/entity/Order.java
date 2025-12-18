package com.education.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String orderNo; // 订单号
    private Integer userId;
    private Integer courseId;
    private BigDecimal amount; // 实际支付金额
    private LocalDateTime createTime;
}
