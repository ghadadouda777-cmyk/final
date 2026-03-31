package com.eventify.repository;

import com.eventify.entity.Comment;
import com.eventify.entity.Event;
import com.eventify.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    
    List<Comment> findByEvent(Event event);
    
    List<Comment> findByAuthor(User author);
    
    List<Comment> findByParentComment(Comment parentComment);
    
    @Query("SELECT c FROM Comment c WHERE c.event = :event AND c.parentComment IS NULL AND c.isDeleted = false ORDER BY c.createdAt ASC")
    List<Comment> findTopLevelCommentsByEvent(@Param("event") Event event);
    
    @Query("SELECT c FROM Comment c WHERE c.parentComment = :parentComment AND c.isDeleted = false ORDER BY c.createdAt ASC")
    List<Comment> findRepliesByParentComment(@Param("parentComment") Comment parentComment);
    
    @Query("SELECT COUNT(c) FROM Comment c WHERE c.event = :event AND c.isDeleted = false")
    long countActiveCommentsByEvent(@Param("event") Event event);
}
