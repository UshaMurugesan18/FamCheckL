package com.familychecklist.repository;

import com.familychecklist.model.PushSubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PushSubscriptionRepository extends JpaRepository<PushSubscription, String> {
    List<PushSubscription> findByMemberId(String memberId);
    void deleteByEndpoint(String endpoint);
}
