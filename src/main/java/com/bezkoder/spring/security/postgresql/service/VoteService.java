package com.bezkoder.spring.security.postgresql.service;

import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface VoteService {
    ResponseEntity<Map<String, Object>> vote(Long userId, Long entityId, String entityType, int value);
    int getVoteValue(Long userId, Long entityId, String entityType);
}
