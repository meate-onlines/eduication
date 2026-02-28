package com.eduication.language.service.impl;

import com.eduication.language.entity.SubscriptionOrder;
import com.eduication.language.enums.PaymentProvider;
import com.eduication.language.service.PaymentGateway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class WechatPaymentGateway implements PaymentGateway {

    @Value("${app.payment.wechat.app-id}")
    private String appId;

    @Value("${app.payment.wechat.mch-id}")
    private String mchId;

    @Value("${app.payment.wechat.notify-url}")
    private String notifyUrl;

    @Override
    public PaymentProvider provider() {
        return PaymentProvider.WECHAT;
    }

    @Override
    public Map<String, Object> createPrepay(SubscriptionOrder order) {
        Map<String, Object> result = new HashMap<>();
        result.put("provider", provider());
        result.put("orderNo", order.getOrderNo());
        result.put("amount", order.getAmount());
        result.put("appId", appId);
        result.put("mchId", mchId);
        result.put("notifyUrl", notifyUrl);
        result.put("codeUrl", "weixin://wxpay/bizpayurl?pr=" + order.getOrderNo());
        result.put("tip", "示例接入：请替换为微信支付统一下单真实调用");
        return result;
    }

    @Override
    public PaymentCallbackResult parseAndVerifyCallback(Map<String, String> payload) {
        String orderNo = payload.get("out_trade_no");
        String tradeNo = payload.get("transaction_id");
        String tradeState = payload.getOrDefault("trade_state", "");
        boolean success = "SUCCESS".equalsIgnoreCase(tradeState);
        String message = success ? "微信支付成功" : "微信支付未成功";
        return new PaymentCallbackResult(success, orderNo, tradeNo, message);
    }
}
