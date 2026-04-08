package com.familychecklist.controller;

import com.familychecklist.service.WebPushService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/push")
@RequiredArgsConstructor
public class PushController {

    private final WebPushService webPushService;

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
        return ResponseEntity.ok().build();
    }
}
