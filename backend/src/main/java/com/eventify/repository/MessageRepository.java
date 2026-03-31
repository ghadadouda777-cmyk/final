package com.eventify.repository;

import com.eventify.entity.Event;
import com.eventify.entity.Message;
import com.eventify.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    
    List<Message> findByEvent(Event event);
    
    List<Message> findBySender(User sender);
    
    List<Message> findByParentMessage(Message parentMessage);
    
    @Query("SELECT m FROM Message m WHERE m.event = :event AND m.parentMessage IS NULL AND m.isDeleted = false ORDER BY m.createdAt ASC")
    List<Message> findTopLevelMessagesByEvent(@Param("event") Event event);
    
    @Query("SELECT m FROM Message m WHERE m.parentMessage = :parentMessage AND m.isDeleted = false ORDER BY m.createdAt ASC")
    List<Message> findRepliesByParentMessage(@Param("parentMessage") Message parentMessage);
    
    @Query("SELECT m FROM Message m WHERE m.event = :event AND m.sender != :currentUser AND m.isRead = false AND m.isDeleted = false ORDER BY m.createdAt ASC")
    List<Message> findUnreadMessages(@Param("event") Event event, @Param("currentUser") User currentUser);
    
    @Query("SELECT COUNT(m) FROM Message m WHERE m.event = :event AND m.isDeleted = false")
    long countActiveMessagesByEvent(@Param("event") Event event);
}
