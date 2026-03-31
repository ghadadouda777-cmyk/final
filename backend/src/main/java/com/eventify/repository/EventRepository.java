package com.eventify.repository;

import com.eventify.entity.Event;
import com.eventify.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    
    List<Event> findByOrganizer(User organizer);
    
    List<Event> findByStatus(Event.Status status);
    
    List<Event> findByCategory(Event.Category category);
    
    @Query("SELECT e FROM Event e WHERE e.startDate >= :startDate AND e.status = 'PUBLISHED' ORDER BY e.startDate ASC")
    List<Event> findUpcomingEvents(@Param("startDate") LocalDateTime startDate);
    
    @Query("SELECT e FROM Event e WHERE e.startDate BETWEEN :startDate AND :endDate AND e.status = 'PUBLISHED'")
    List<Event> findEventsBetweenDates(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT e FROM Event e WHERE LOWER(e.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(e.description) LIKE LOWER(CONCAT('%', :keyword, '%')) AND e.status = 'PUBLISHED'")
    List<Event> searchEvents(@Param("keyword") String keyword);
    
    @Query("SELECT e FROM Event e JOIN e.tags t WHERE t IN :tags AND e.status = 'PUBLISHED'")
    List<Event> findByTags(@Param("tags") List<String> tags);
    
    @Query("SELECT COUNT(e) FROM Event e WHERE e.organizer = :organizer")
    long countByOrganizer(@Param("organizer") User organizer);
    
    @Query("SELECT e FROM Event e WHERE e.isOnline = true AND e.status = 'PUBLISHED'")
    List<Event> findOnlineEvents();
    
    @Query("SELECT e FROM Event e WHERE e.location.city = :city AND e.status = 'PUBLISHED'")
    List<Event> findByCity(@Param("city") String city);
}
