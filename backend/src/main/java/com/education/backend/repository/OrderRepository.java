package com.education.backend.repository;

import com.education.backend.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    // 根据订单号查询订单
    Order findByOrderNo(String orderNo);
}