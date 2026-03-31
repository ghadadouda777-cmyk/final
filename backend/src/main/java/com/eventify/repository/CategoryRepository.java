package com.eventify.repository;

import com.eventify.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    
    List<Category> findByIsActive(Boolean isActive);
    
    @Query("SELECT c FROM Category c WHERE c.isActive = true ORDER BY c.eventCount DESC")
    List<Category> findActiveCategoriesOrderByEventCount();
    
    @Query("SELECT c FROM Category c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Category> searchCategories(@Param("keyword") String keyword);
}
