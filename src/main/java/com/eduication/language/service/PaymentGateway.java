package com.eduication.language.service;

import com.eduication.language.entity.SubscriptionOrder;
import com.eduication.language.enums.PaymentProvider;

import java.util.Map;

public interface PaymentGateway {

    PaymentProvider provider();

    Map<String, Object> createPrepay(SubscriptionOrder order);

    PaymentCallbackResult parseAndVerifyCallback(Map<String, String> payload);

    record PaymentCallbackResult(boolean success, String orderNo, String providerTradeNo, String message) {
    }
}
