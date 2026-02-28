package com.eduication.language.service;

import com.eduication.language.entity.MembershipPlan;
import com.eduication.language.entity.SubscriptionOrder;
import com.eduication.language.entity.UserAccount;
import com.eduication.language.enums.OrderStatus;
import com.eduication.language.enums.PaymentProvider;
import com.eduication.language.exception.BusinessException;
import com.eduication.language.repository.MembershipPlanRepository;
import com.eduication.language.repository.SubscriptionOrderRepository;
import com.eduication.language.repository.UserAccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class MembershipService {

    private final MembershipPlanRepository membershipPlanRepository;
    private final SubscriptionOrderRepository subscriptionOrderRepository;
    private final UserAccountRepository userAccountRepository;
    private final Map<PaymentProvider, PaymentGateway> gatewayMap;

    public MembershipService(MembershipPlanRepository membershipPlanRepository,
                             SubscriptionOrderRepository subscriptionOrderRepository,
                             UserAccountRepository userAccountRepository,
                             List<PaymentGateway> gateways) {
        this.membershipPlanRepository = membershipPlanRepository;
        this.subscriptionOrderRepository = subscriptionOrderRepository;
        this.userAccountRepository = userAccountRepository;
        this.gatewayMap = gateways.stream().collect(Collectors.toMap(PaymentGateway::provider, Function.identity()));
    }

    public List<MembershipPlan> listActivePlans() {
        return membershipPlanRepository.findByActiveTrueOrderByPriceAsc();
    }

    public MembershipPlan createPlan(String name, BigDecimal price, Integer durationDays, String description) {
        MembershipPlan plan = new MembershipPlan();
        plan.setName(name);
        plan.setPrice(price);
        plan.setDurationDays(durationDays);
        plan.setDescription(description);
        plan.setActive(true);
        return membershipPlanRepository.save(plan);
    }

    @Transactional
    public Map<String, Object> createSubscriptionOrder(UserAccount user, Long planId, PaymentProvider provider) {
        MembershipPlan plan = membershipPlanRepository.findById(planId)
                .orElseThrow(() -> new BusinessException("会员套餐不存在"));
        if (!plan.isActive()) {
            throw new BusinessException("该会员套餐已下架");
        }
        SubscriptionOrder order = new SubscriptionOrder();
        order.setOrderNo(generateOrderNo(provider));
        order.setUser(user);
        order.setPlan(plan);
        order.setProvider(provider);
        order.setAmount(plan.getPrice());
        order.setStatus(OrderStatus.CREATED);
        SubscriptionOrder saved = subscriptionOrderRepository.save(order);

        PaymentGateway gateway = gatewayMap.get(provider);
        if (gateway == null) {
            throw new BusinessException("暂不支持该支付渠道");
        }
        return gateway.createPrepay(saved);
    }

    @Transactional
    public String handlePaymentCallback(PaymentProvider provider, Map<String, String> payload) {
        PaymentGateway gateway = gatewayMap.get(provider);
        if (gateway == null) {
            throw new BusinessException("支付渠道不存在");
        }
        PaymentGateway.PaymentCallbackResult callbackResult = gateway.parseAndVerifyCallback(payload);
        if (!callbackResult.success()) {
            return callbackResult.message();
        }
        if (callbackResult.orderNo() == null || callbackResult.orderNo().isBlank()) {
            throw new BusinessException("订单号为空");
        }
        SubscriptionOrder order = subscriptionOrderRepository.findByOrderNo(callbackResult.orderNo())
                .orElseThrow(() -> new BusinessException("订单不存在"));
        if (order.getStatus() == OrderStatus.PAID) {
            return "订单已处理";
        }
        order.setStatus(OrderStatus.PAID);
        order.setProviderTradeNo(callbackResult.providerTradeNo());
        order.setPaidAt(LocalDateTime.now());
        subscriptionOrderRepository.save(order);
        activateVip(order.getUser(), order.getPlan().getDurationDays());
        return "支付成功，会员已开通";
    }

    public List<SubscriptionOrder> listOrders(Long userId) {
        return subscriptionOrderRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    @Transactional
    public void activateVip(UserAccount user, Integer durationDays) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime base = user.getVipExpireAt() != null && user.getVipExpireAt().isAfter(now)
                ? user.getVipExpireAt() : now;
        user.setVip(true);
        user.setVipExpireAt(base.plusDays(durationDays));
        userAccountRepository.save(user);
    }

    private String generateOrderNo(PaymentProvider provider) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        int random = ThreadLocalRandom.current().nextInt(10000, 99999);
        return provider.name().substring(0, 1) + timestamp + random;
    }
}
