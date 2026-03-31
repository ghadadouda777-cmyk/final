package com.eventify.repository;

import com.eventify.entity.Event;
import com.eventify.entity.Registration;
import com.eventify.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RegistrationRepository extends JpaRepository<Registration, Long> {
    
    List<Registration> findByUser(User user);
    
    List<Registration> findByEvent(Event event);
    
    Optional<Registration> findByUserAndEvent(User user, Event event);
    
    List<Registration> findByStatus(Registration.RegistrationStatus status);
    
    @Query("SELECT COUNT(r) FROM Registration r WHERE r.event = :event AND r.status = 'REGISTERED'")
    long countRegisteredUsers(@Param("event") Event event);
    
    @Query("SELECT r FROM Registration r WHERE r.user = :user AND r.event.status = 'PUBLISHED'")
    List<Registration> findUserUpcomingRegistrations(@Param("user") User user);
    
    @Query("SELECT r FROM Registration r WHERE r.event.organizer = :organizer")
    List<Registration> findByEventOrganizer(@Param("organizer") User organizer);
    
    @Query("SELECT COUNT(r) FROM Registration r WHERE r.user = :user AND r.status = 'ATTENDED'")
    long countAttendedEvents(@Param("user") User user);
}
