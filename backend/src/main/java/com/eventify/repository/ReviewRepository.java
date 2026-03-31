package com.eventify.repository;

import com.eventify.entity.Event;
import com.eventify.entity.Review;
import com.eventify.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    
    List<Review> findByEvent(Event event);
    
    List<Review> findByReviewer(User reviewer);
    
    Optional<Review> findByReviewerAndEvent(User reviewer, Event event);
    
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.event = :event AND r.isVerified = true")
    Double getAverageRatingForEvent(@Param("event") Event event);
    
    @Query("SELECT COUNT(r) FROM Review r WHERE r.event = :event AND r.isVerified = true")
    long countVerifiedReviewsForEvent(@Param("event") Event event);
    
    @Query("SELECT r FROM Review r WHERE r.event = :event AND r.isVerified = true ORDER BY r.createdAt DESC")
    List<Review> findVerifiedReviewsByEvent(@Param("event") Event event);
    
    @Query("SELECT r FROM Review r WHERE r.rating >= :minRating AND r.isVerified = true")
    List<Review> findByMinRating(@Param("minRating") Integer minRating);
}
