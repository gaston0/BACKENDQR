package com.bezkoder.spring.security.postgresql.repository;

import com.bezkoder.spring.security.postgresql.models.User;
import com.bezkoder.spring.security.postgresql.models.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface VoteRepository extends JpaRepository<Vote,Long> {
    Vote findByUserAndEntityIdAndEntityType(User user, Long entityId, String entityType);

    @Query("SELECT SUM(v.value) FROM Vote v WHERE v.entityId = :entityId")
    Integer sumValuesByEntityId(@Param("entityId") Long entityId);

    @Query("SELECT SUM(v.value) FROM Vote v WHERE v.question.id = :questionId")
    Integer sumVotesByQuestionId(@Param("questionId") Long questionId);

    @Query("SELECT COALESCE(SUM(v.value), 0) FROM Vote v WHERE v.answer.id = :answerId")
    Integer sumVotesByAnswerId(@Param("answerId") Long answerId);


    @Query("SELECT SUM(v.value) FROM Vote v WHERE v.answerResponse.id = :answerResponseId")
    Integer sumVotesByAnswerResponseId(@Param("answerResponseId") Long answerResponseId);



    @Query("SELECT COALESCE(SUM(v.value), 0) FROM Vote v WHERE v.entityId = :entityId AND v.entityType = 'ANSWER'")
    int sumValuesByEntityIds(@Param("entityId") Long entityId);



}
