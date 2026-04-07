package com.familychecklist.controller;

import com.familychecklist.service.SseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/sse")
@RequiredArgsConstructor
public class SseController {

    private final SseService sseService;

    @GetMapping("/family/{familyId}")
    public SseEmitter subscribeFamily(@PathVariable String familyId) {
        return sseService.subscribeFamily(familyId);
    }

    @GetMapping("/member/{memberId}")
    public SseEmitter subscribeMember(@PathVariable String memberId) {
        return sseService.subscribeMember(memberId);
    }
}
