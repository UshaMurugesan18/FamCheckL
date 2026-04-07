package com.familychecklist.controller;

import com.familychecklist.model.*;
import com.familychecklist.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/families")
@RequiredArgsConstructor
public class FamilyController {

    private final FamilyRepository familyRepo;
    private final MemberRepository memberRepo;

    @GetMapping
    public List<Family> getAll() {
        return familyRepo.findAllByOrderByCreatedAtDesc();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Family> getOne(@PathVariable String id) {
        return familyRepo.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Family create(@RequestBody Map<String, String> body) {
        return familyRepo.save(Family.builder()
            .familyName(body.get("familyName"))
            .build());
    }

    // Create family + creator member in one call
    @PostMapping("/setup")
    public Map<String, String> setup(@RequestBody Map<String, String> body) {
        Family family = familyRepo.save(Family.builder()
            .familyName(body.get("familyName"))
            .build());
        Member creator = memberRepo.save(Member.builder()
            .familyId(family.getId())
            .member(body.get("creatorName"))
            .role(1)
            .email(body.getOrDefault("email", ""))
            .build());
        Map<String, String> resp = new HashMap<>();
        resp.put("familyId", family.getId());
        resp.put("creatorId", creator.getId());
        return resp;
    }

    // Members under a family
    @GetMapping("/{id}/members")
    public List<Member> getMembers(@PathVariable String id) {
        return memberRepo.findByFamilyId(id);
    }
}
