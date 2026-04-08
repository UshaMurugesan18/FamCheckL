package com.familychecklist.controller;

import com.familychecklist.model.PushSubscription;
import com.familychecklist.repository.PushSubscriptionRepository;
import com.familychecklist.service.WebPushService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/push")
@RequiredArgsConstructor
@Slf4j
public class PushController {

    private final WebPushService webPushService;
    private final PushSubscriptionRepository pushRepo;

    @PostMapping("/subscribe")
    public ResponseEntity<Void> subscribe(@RequestBody Map<String, Object> body) {
        String memberId = (String) body.get("memberId");

        @SuppressWarnings("unchecked")
        Map<String, Object> subscription = (Map<String, Object>) body.get("subscription");
        String endpoint = (String) subscription.get("endpoint");

        @SuppressWarnings("unchecked")
        Map<String, String> keys = (Map<String, String>) subscription.get("keys");
        String p256dh = keys.get("p256dh");
        String auth = keys.get("auth");

        webPushService.saveSubscription(memberId, endpoint, p256dh, auth);
        log.info("[Push] Subscription saved for memberId={} endpoint={}...", memberId,
            endpoint != null && endpoint.length() > 40 ? endpoint.substring(0, 40) : endpoint);
        return ResponseEntity.ok().build();
    }

    /** Check how many subscriptions are saved for a member */
    @GetMapping("/subscriptions/{memberId}")
    public ResponseEntity<List<PushSubscription>> getSubscriptions(@PathVariable String memberId) {
        return ResponseEntity.ok(pushRepo.findByMemberId(memberId));
    }

    /** Manually fire a test push to a member — use to verify the pipeline works */
    @PostMapping("/test/{memberId}")
    public ResponseEntity<String> testPush(@PathVariable String memberId) {
        List<PushSubscription> subs = pushRepo.findByMemberId(memberId);
        if (subs.isEmpty()) {
            return ResponseEntity.ok("NO_SUBSCRIPTIONS — member has not subscribed yet");
        }
        webPushService.sendPush(memberId, "🔔 Test Alarm", "Push is working! Tasks are waiting.", "/receiver/" + memberId);
        return ResponseEntity.ok("SENT to " + subs.size() + " subscription(s)");
    }
}
