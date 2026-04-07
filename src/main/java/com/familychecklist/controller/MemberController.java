package com.familychecklist.controller;

import com.familychecklist.model.Member;
import com.familychecklist.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberRepository memberRepo;

    @GetMapping("/{id}")
    public ResponseEntity<Member> getOne(@PathVariable String id) {
        return memberRepo.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/by-email")
    public ResponseEntity<Member> byEmail(@RequestParam String email) {
        return memberRepo.findByEmail(email)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Member create(@RequestBody Map<String, Object> body) {
        return memberRepo.save(Member.builder()
            .familyId((String) body.get("familyId"))
            .member((String) body.get("member"))
            .role(body.containsKey("role") ? (Integer) body.get("role") : 2)
            .email((String) body.getOrDefault("email", ""))
            .build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        memberRepo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
