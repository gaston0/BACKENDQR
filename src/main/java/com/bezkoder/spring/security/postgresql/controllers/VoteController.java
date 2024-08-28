package com.bezkoder.spring.security.postgresql.controllers;

import com.bezkoder.spring.security.postgresql.repository.VoteRepository;
import com.bezkoder.spring.security.postgresql.service.VoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/votes")
public class VoteController {

    @Autowired
    private VoteService voteService;

    @Autowired
    private VoteRepository voteRepository;

    @PostMapping("/{entityType}/{entityId}")
    public ResponseEntity<Map<String, Object>> vote(
            @PathVariable Long entityId,
            @PathVariable String entityType,
            @RequestParam Long userId,
            @RequestParam int value) {

        // Call the vote service and get the response map containing the updated votes and user vote
        ResponseEntity<Map<String, Object>> voteResponse = voteService.vote(userId, entityId, entityType, value);

        // Return the response directly from the service
        return voteResponse;
    }

    @GetMapping("/status")
    public ResponseEntity<Map<String, Integer>> getVoteStatus(
            @RequestParam Long entityId,
            @RequestParam String entityType,
            @RequestParam Long userId) {

        int voteValue = voteService.getVoteValue(userId, entityId, entityType);

        // Safely handle null value from repository
        Integer totalVotesFromRepo = voteRepository.sumValuesByEntityId(entityId);
        int totalVotes = (totalVotesFromRepo != null) ? totalVotesFromRepo : 0;

        Map<String, Integer> response = new HashMap<>();
        response.put("value", voteValue);
        response.put("totalVotes", totalVotes);

        return ResponseEntity.ok(response);
    }
}
