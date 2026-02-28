package com.eduication.language.controller;

import com.eduication.language.dto.ApiResponse;
import com.eduication.language.entity.MembershipPlan;
import com.eduication.language.entity.SubscriptionOrder;
import com.eduication.language.entity.UserAccount;
import com.eduication.language.enums.OrderStatus;
import com.eduication.language.enums.PaymentProvider;
import com.eduication.language.service.CurrentUserService;
import com.eduication.language.service.MembershipService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/membership")
public class MembershipController {

    private final MembershipService membershipService;
    private final CurrentUserService currentUserService;

    public MembershipController(MembershipService membershipService,
                                CurrentUserService currentUserService) {
        this.membershipService = membershipService;
        this.currentUserService = currentUserService;
    }

    @GetMapping("/plans")
    public ApiResponse<List<MembershipPlan>> plans() {
        return ApiResponse.ok("查询成功", membershipService.listActivePlans());
    }

    @PostMapping("/plans")
    public ApiResponse<MembershipPlan> createPlan(@Valid @RequestBody CreatePlanRequest request) {
        MembershipPlan plan = membershipService.createPlan(
                request.name(), request.price(), request.durationDays(), request.description());
        return ApiResponse.ok("创建成功", plan);
    }

    @PostMapping("/subscribe")
    public ApiResponse<Map<String, Object>> subscribe(@Valid @RequestBody SubscribeRequest request) {
        UserAccount user = currentUserService.requireCurrentUser();
        Map<String, Object> prepay = membershipService.createSubscriptionOrder(
                user, request.planId(), request.provider());
        return ApiResponse.ok("下单成功", prepay);
    }

    @GetMapping("/orders")
    public ApiResponse<List<OrderResponse>> orders() {
        UserAccount user = currentUserService.requireCurrentUser();
        List<OrderResponse> result = membershipService.listOrders(user.getId())
                .stream()
                .map(this::toOrderResponse)
                .toList();
        return ApiResponse.ok("查询成功", result);
    }

    @GetMapping("/me")
    public ApiResponse<Map<String, Object>> me() {
        UserAccount user = currentUserService.requireCurrentUser();
        Map<String, Object> data = new HashMap<>();
        data.put("userId", user.getId());
        data.put("vip", user.isVip());
        data.put("vipExpireAt", user.getVipExpireAt());
        return ApiResponse.ok("查询成功", data);
    }

    public record CreatePlanRequest(
            @NotBlank(message = "套餐名不能为空")
            String name,
            @NotNull(message = "价格不能为空")
            @DecimalMin(value = "0.01", message = "价格必须大于0")
            BigDecimal price,
            @NotNull(message = "时长不能为空")
            @Min(value = 1, message = "时长至少1天")
            Integer durationDays,
            String description
    ) {
    }

    public record SubscribeRequest(
            @NotNull(message = "套餐ID不能为空")
            Long planId,
            @NotNull(message = "支付渠道不能为空")
            PaymentProvider provider
    ) {
    }

    public record OrderResponse(
            String orderNo,
            String planName,
            PaymentProvider provider,
            OrderStatus status,
            BigDecimal amount,
            LocalDateTime paidAt,
            LocalDateTime createdAt
    ) {
    }

    private OrderResponse toOrderResponse(SubscriptionOrder order) {
        return new OrderResponse(
                order.getOrderNo(),
                order.getPlan().getName(),
                order.getProvider(),
                order.getStatus(),
                order.getAmount(),
                order.getPaidAt(),
                order.getCreatedAt()
        );
    }
}
