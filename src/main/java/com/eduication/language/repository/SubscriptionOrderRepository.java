package com.eduication.language.repository;

import com.eduication.language.entity.SubscriptionOrder;
import com.eduication.language.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubscriptionOrderRepository extends JpaRepository<SubscriptionOrder, Long> {
    Optional<SubscriptionOrder> findByOrderNo(String orderNo);

    List<SubscriptionOrder> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<SubscriptionOrder> findByStatus(OrderStatus status);
}
