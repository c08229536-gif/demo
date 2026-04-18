package com.education.backend.controller;

import com.education.backend.service.OrderService;
import com.education.backend.service.UserService;
import com.education.backend.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/payment")
public class MockPaymentController {

    @Autowired
    private OrderService orderService;
    @Autowired
    private UserService userService;

    @PostMapping("/buy")
    public String buyCourse(@RequestBody BuyRequest request) {
        return orderService.buyCourse(request.getUserId(), request.getCourseId());
    }

    @PostMapping("/recharge")
    public String recharge(@RequestBody RechargeRequest request) {
        userService.recharge(request.getUserId(), request.getAmount());
        User user = userService.findById(request.getUserId());
        return "充值成功，当前余额：" + user.getBalance();
    }

    @lombok.Data
    static class BuyRequest {
        private Integer userId;
        private Integer courseId;
    }

    @lombok.Data
    static class RechargeRequest {
        private Integer userId;
        private BigDecimal amount;
    }
}