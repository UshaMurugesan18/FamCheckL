package com.familychecklist.service;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Server-Sent Events service — replaces Firestore onSnapshot real-time listeners.
 * React clients subscribe to /api/sse/family/{familyId} or /api/sse/member/{memberId}
 * and receive an "update" event whenever data changes.
 */
@Service
public class SseService {

    private final Map<String, List<SseEmitter>> familyEmitters = new HashMap<>();
    private final Map<String, List<SseEmitter>> memberEmitters = new HashMap<>();

    public SseEmitter subscribeFamily(String familyId) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        familyEmitters.computeIfAbsent(familyId, k -> new CopyOnWriteArrayList<>()).add(emitter);
        emitter.onCompletion(() -> remove(familyEmitters, familyId, emitter));
        emitter.onTimeout(() -> remove(familyEmitters, familyId, emitter));
        return emitter;
    }

    public SseEmitter subscribeMember(String memberId) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        memberEmitters.computeIfAbsent(memberId, k -> new CopyOnWriteArrayList<>()).add(emitter);
        emitter.onCompletion(() -> remove(memberEmitters, memberId, emitter));
        emitter.onTimeout(() -> remove(memberEmitters, memberId, emitter));
        return emitter;
    }

    public void notifyFamily(String familyId) {
        send(familyEmitters, familyId);
    }

    public void notifyMember(String memberId) {
        send(memberEmitters, memberId);
    }

    private void send(Map<String, List<SseEmitter>> map, String key) {
        List<SseEmitter> emitters = map.getOrDefault(key, Collections.emptyList());
        List<SseEmitter> dead = new ArrayList<>();
        for (SseEmitter e : emitters) {
            try {
                e.send(SseEmitter.event().name("update").data("refresh"));
            } catch (IOException ex) {
                dead.add(e);
            }
        }
        emitters.removeAll(dead);
    }

    private void remove(Map<String, List<SseEmitter>> map, String key, SseEmitter emitter) {
        List<SseEmitter> list = map.get(key);
        if (list != null) list.remove(emitter);
    }
}
