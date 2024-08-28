package com.bezkoder.spring.security.postgresql.service;

import com.bezkoder.spring.security.postgresql.models.*;
import com.bezkoder.spring.security.postgresql.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class VoteServiceImp implements VoteService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private AnswerRepository answerRepository;

    @Autowired
    private AnswerResponseRepository answerResponseRepository;

    @Autowired
    private VoteRepository voteRepository;

    public ResponseEntity<Map<String, Object>> vote(Long userId, Long entityId, String entityType, int value) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Error: User is not found."));

        Vote vote = voteRepository.findByUserAndEntityIdAndEntityType(user, entityId, entityType);

        if (vote != null) {
            if (vote.getValue() == value) {
                // User clicked the same vote again, remove the vote
                voteRepository.delete(vote);
            } else {
                // Update the existing vote
                vote.setValue(value);
                voteRepository.save(vote);
            }
        } else {
            if (value >= -1 && value <= 1) {
                // Create a new vote
                vote = new Vote();
                vote.setUser(user);
                vote.setEntityId(entityId);
                vote.setEntityType(entityType);
                vote.setValue(value);

                switch (entityType) {
                    case "Question":
                        Question question = questionRepository.findById(entityId)
                                .orElseThrow(() -> new RuntimeException("Error: Question is not found."));
                        vote.setQuestion(question);
                        break;
                    case "Answer":
                        Answer answer = answerRepository.findById(entityId)
                                .orElseThrow(() -> new RuntimeException("Error: Answer is not found."));
                        vote.setAnswer(answer);
                        break;
                    case "AnswerResponse":
                        AnswerResponse answerResponse = answerResponseRepository.findById(entityId)
                                .orElseThrow(() -> new RuntimeException("Error: AnswerResponse is not found."));
                        vote.setAnswerResponse(answerResponse);
                        break;
                    default:
                        throw new RuntimeException("Error: Invalid entityType.");
                }

                voteRepository.save(vote);
            } else {
                throw new RuntimeException("Error: Vote value is not valid.");
            }
        }

        // Calculate the updated vote count
        int updatedVotes = calculateUpdatedVotes(entityId, entityType);

        // Prepare the response data
        Map<String, Object> response = new HashMap<>();
        response.put("updatedVotes", updatedVotes);
        response.put("userVote", vote != null ? vote.getValue() : null);

        return ResponseEntity.ok(response);
    }

    private int calculateUpdatedVotes(Long entityId, String entityType) {
        Integer sumVotes = null;

        switch (entityType) {
            case "Question":
                sumVotes = voteRepository.sumVotesByQuestionId(entityId);
                break;
            case "Answer":
                sumVotes = voteRepository.sumVotesByAnswerId(entityId);
                break;
            case "AnswerResponse":
                sumVotes = voteRepository.sumVotesByAnswerResponseId(entityId);
                break;
            default:
                throw new RuntimeException("Error: Invalid entityType.");
        }

        // Handle the null case by returning 0 if sumVotes is null
        return (sumVotes != null) ? sumVotes : 0;
    }




    public int getVoteValue(Long userId, Long entityId, String entityType) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Error: User is not found."));
        Vote vote = voteRepository.findByUserAndEntityIdAndEntityType(user, entityId, entityType);
        return vote != null ? vote.getValue() : -1; // Assuming -1 means no vote
    }
}
