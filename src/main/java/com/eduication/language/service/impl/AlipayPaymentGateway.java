package com.eduication.language.service.impl;

import com.eduication.language.entity.SubscriptionOrder;
import com.eduication.language.enums.PaymentProvider;
import com.eduication.language.service.PaymentGateway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AlipayPaymentGateway implements PaymentGateway {

    @Value("${app.payment.alipay.app-id}")
    private String appId;

    @Value("${app.payment.alipay.notify-url}")
    private String notifyUrl;

    @Override
    public PaymentProvider provider() {
        return PaymentProvider.ALIPAY;
    }

    @Override
    public Map<String, Object> createPrepay(SubscriptionOrder order) {
        Map<String, Object> result = new HashMap<>();
        result.put("provider", provider());
        result.put("orderNo", order.getOrderNo());
        result.put("amount", order.getAmount());
        result.put("appId", appId);
        result.put("notifyUrl", notifyUrl);
        result.put("payUrl", "https://openapi.alipay.com/gateway.do?out_trade_no=" + order.getOrderNo());
        result.put("tip", "示例接入：请替换为支付宝下单真实调用");
        return result;
    }

    @Override
    public PaymentCallbackResult parseAndVerifyCallback(Map<String, String> payload) {
        String orderNo = payload.get("out_trade_no");
        String tradeNo = payload.get("trade_no");
        String tradeStatus = payload.getOrDefault("trade_status", "");
        boolean success = "TRADE_SUCCESS".equalsIgnoreCase(tradeStatus)
                || "TRADE_FINISHED".equalsIgnoreCase(tradeStatus);
        String message = success ? "支付宝支付成功" : "支付宝支付未成功";
        return new PaymentCallbackResult(success, orderNo, tradeNo, message);
    }
}
