package com.familychecklist.service;

import com.familychecklist.model.PushSubscription;
import com.familychecklist.repository.PushSubscriptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.martijndwars.webpush.Notification;
import nl.martijndwars.webpush.PushService;
import nl.martijndwars.webpush.Subscription;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.security.Security;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebPushService {

    @Value("${vapid.public.key}")
    private String vapidPublicKey;

    @Value("${vapid.private.key}")
    private String vapidPrivateKey;

    private final PushSubscriptionRepository pushRepo;
    private PushService pushService;

    @PostConstruct
    public void init() throws Exception {
        if (Security.getProvider("BC") == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
        pushService = new PushService(vapidPublicKey, vapidPrivateKey);
    }

    public void saveSubscription(String memberId, String endpoint, String p256dh, String auth) {
        // Remove old subscription with same endpoint if exists
        pushRepo.deleteByEndpoint(endpoint);
        pushRepo.save(PushSubscription.builder()
            .memberId(memberId)
            .endpoint(endpoint)
            .p256dh(p256dh)
            .auth(auth)
            .build());
    }

    public void sendPush(String memberId, String title, String body, String url) {
        List<PushSubscription> subs = pushRepo.findByMemberId(memberId);
        for (PushSubscription sub : subs) {
            try {
                String payload = String.format(
                    "{\"title\":\"%s\",\"body\":\"%s\",\"tag\":\"%s\",\"url\":\"%s\"}",
                    escapeJson(title), escapeJson(body), memberId, url == null ? "/" : url
                );
                Subscription subscription = new Subscription(
                    sub.getEndpoint(),
                    new Subscription.Keys(sub.getP256dh(), sub.getAuth())
                );
                Notification notification = new Notification(subscription, payload);
                pushService.send(notification);
            } catch (Exception e) {
                log.warn("Failed to send push to {}: {}", sub.getEndpoint(), e.getMessage());
                // Remove expired/invalid subscriptions
                if (e.getMessage() != null &&
                    (e.getMessage().contains("410") || e.getMessage().contains("404"))) {
                    pushRepo.deleteByEndpoint(sub.getEndpoint());
                }
            }
        }
    }

    private String escapeJson(String s) {
        return s == null ? "" : s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
