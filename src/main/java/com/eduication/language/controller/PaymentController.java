package com.eduication.language.controller;

import com.eduication.language.dto.ApiResponse;
import com.eduication.language.enums.PaymentProvider;
import com.eduication.language.service.MembershipService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final MembershipService membershipService;

    public PaymentController(MembershipService membershipService) {
        this.membershipService = membershipService;
    }

    @PostMapping("/wechat/callback")
    public ApiResponse<String> wechatCallback(@RequestParam Map<String, String> payload) {
        String result = membershipService.handlePaymentCallback(PaymentProvider.WECHAT, payload);
        return ApiResponse.ok(result, result);
    }

    @PostMapping("/alipay/callback")
    public ApiResponse<String> alipayCallback(@RequestParam Map<String, String> payload) {
        String result = membershipService.handlePaymentCallback(PaymentProvider.ALIPAY, payload);
        return ApiResponse.ok(result, result);
    }
}
