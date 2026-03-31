package com.eventify.dto;

import com.eventify.entity.Event;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.List;

public class EventUpdateRequest {
    @NotBlank(message = "Event title is required")
    @Size(max = 100, message = "Title cannot exceed 100 characters")
    private String title;

    @NotBlank(message = "Event description is required")
    @Size(max = 2000, message = "Description cannot exceed 2000 characters")
    private String description;

    @NotNull(message = "Start date is required")
    private LocalDateTime startDate;

    @NotNull(message = "End date is required")
    private LocalDateTime endDate;

    @Min(value = 0, message = "Price cannot be negative")
    private Double price = 0.0;

    @NotNull(message = "Location is required")
    private EventCreateRequest.LocationDto location;

    @NotNull(message = "Category is required")
    private Event.Category category;

    @Min(value = 1, message = "Capacity must be at least 1")
    private Integer capacity;

    private LocalDateTime registrationDeadline;

    private List<String> tags;

    private Boolean isOnline = false;

    private String meetingLink;

    // Getters and Setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getStartDate() { return startDate; }
    public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }

    public LocalDateTime getEndDate() { return endDate; }
    public void setEndDate(LocalDateTime endDate) { this.endDate = endDate; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public EventCreateRequest.LocationDto getLocation() { return location; }
    public void setLocation(EventCreateRequest.LocationDto location) { this.location = location; }

    public Event.Category getCategory() { return category; }
    public void setCategory(Event.Category category) { this.category = category; }

    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }

    public LocalDateTime getRegistrationDeadline() { return registrationDeadline; }
    public void setRegistrationDeadline(LocalDateTime registrationDeadline) { this.registrationDeadline = registrationDeadline; }

    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }

    public Boolean getIsOnline() { return isOnline; }
    public void setIsOnline(Boolean isOnline) { this.isOnline = isOnline; }

    public String getMeetingLink() { return meetingLink; }
    public void setMeetingLink(String meetingLink) { this.meetingLink = meetingLink; }
}
