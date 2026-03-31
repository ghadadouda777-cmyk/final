package com.eventify.controller;

import com.eventify.entity.Event;
import com.eventify.entity.User;
import com.eventify.service.EventService;
import com.eventify.service.AuthService;
import com.eventify.dto.EventCreateRequest;
import com.eventify.dto.EventUpdateRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/events")
@CrossOrigin(origins = "${cors.allowed-origins}")
public class EventController {

    @Autowired
    private EventService eventService;

    @Autowired
    private AuthService authService;

    @GetMapping
    public ResponseEntity<Page<Event>> getAllEvents(Pageable pageable) {
        Page<Event> events = eventService.getPublishedEvents(pageable);
        return ResponseEntity.ok(events);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Event> getEventById(@PathVariable Long id) {
        Event event = eventService.getEventById(id);
        return ResponseEntity.ok(event);
    }

    @PostMapping
    public ResponseEntity<Event> createEvent(
            @Valid @RequestPart("event") EventCreateRequest request,
            @RequestPart(value = "bannerImage", required = false) MultipartFile bannerImage) {
        
        User currentUser = authService.getCurrentUser();
        
        // Check if user is authorized to create events
        if (currentUser.getRole() != User.Role.ORGANIZER && currentUser.getRole() != User.Role.ADMIN) {
            throw new RuntimeException("Not authorized to create events");
        }

        Event event = convertCreateRequestToEvent(request);
        Event createdEvent = eventService.createEvent(event, bannerImage, currentUser);
        
        return ResponseEntity.ok(createdEvent);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Event> updateEvent(
            @PathVariable Long id,
            @Valid @RequestPart("event") EventUpdateRequest request,
            @RequestPart(value = "bannerImage", required = false) MultipartFile bannerImage) {
        
        User currentUser = authService.getCurrentUser();
        
        Event eventDetails = convertUpdateRequestToEvent(request);
        Event updatedEvent = eventService.updateEvent(id, eventDetails, bannerImage, currentUser);
        
        return ResponseEntity.ok(updatedEvent);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        User currentUser = authService.getCurrentUser();
        eventService.deleteEvent(id, currentUser);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/publish")
    public ResponseEntity<Event> publishEvent(@PathVariable Long id) {
        User currentUser = authService.getCurrentUser();
        Event event = eventService.publishEvent(id, currentUser);
        return ResponseEntity.ok(event);
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<Event> cancelEvent(@PathVariable Long id) {
        User currentUser = authService.getCurrentUser();
        Event event = eventService.cancelEvent(id, currentUser);
        return ResponseEntity.ok(event);
    }

    @GetMapping("/my-events")
    public ResponseEntity<List<Event>> getMyEvents() {
        User currentUser = authService.getCurrentUser();
        List<Event> events = eventService.getEventsByOrganizer(currentUser);
        return ResponseEntity.ok(events);
    }

    @GetMapping("/upcoming")
    public ResponseEntity<List<Event>> getUpcomingEvents() {
        List<Event> events = eventService.getUpcomingEvents();
        return ResponseEntity.ok(events);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Event>> searchEvents(@RequestParam String keyword) {
        List<Event> events = eventService.searchEvents(keyword);
        return ResponseEntity.ok(events);
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<Event>> getEventsByCategory(@PathVariable Event.Category category) {
        List<Event> events = eventService.getEventsByCategory(category);
        return ResponseEntity.ok(events);
    }

    @GetMapping("/online")
    public ResponseEntity<List<Event>> getOnlineEvents() {
        List<Event> events = eventService.getOnlineEvents();
        return ResponseEntity.ok(events);
    }

    @GetMapping("/city/{city}")
    public ResponseEntity<List<Event>> getEventsByCity(@PathVariable String city) {
        List<Event> events = eventService.getEventsByCity(city);
        return ResponseEntity.ok(events);
    }

    @GetMapping("/between-dates")
    public ResponseEntity<List<Event>> getEventsBetweenDates(
            @RequestParam LocalDateTime startDate,
            @RequestParam LocalDateTime endDate) {
        List<Event> events = eventService.getEventsBetweenDates(startDate, endDate);
        return ResponseEntity.ok(events);
    }

    @GetMapping("/tags")
    public ResponseEntity<List<Event>> getEventsByTags(@RequestParam List<String> tags) {
        List<Event> events = eventService.getEventsByTags(tags);
        return ResponseEntity.ok(events);
    }

    // Helper methods to convert DTOs to entities
    private Event convertCreateRequestToEvent(EventCreateRequest request) {
        Event event = new Event();
        event.setTitle(request.getTitle());
        event.setDescription(request.getDescription());
        event.setStartDate(request.getStartDate());
        event.setEndDate(request.getEndDate());
        event.setPrice(request.getPrice());
        event.setCapacity(request.getCapacity());
        event.setRegistrationDeadline(request.getRegistrationDeadline());
        event.setTags(request.getTags());
        event.setCategory(request.getCategory());
        event.setIsOnline(request.getIsOnline());
        event.setMeetingLink(request.getMeetingLink());
        
        // Set location
        Event.Location location = new Event.Location();
        location.setAddress(request.getLocation().getAddress());
        location.setCity(request.getLocation().getCity());
        location.setCountry(request.getLocation().getCountry());
        if (request.getLocation().getCoordinates() != null) {
            Event.Location.Coordinates coordinates = new Event.Location.Coordinates();
            coordinates.setLat(request.getLocation().getCoordinates().getLat());
            coordinates.setLng(request.getLocation().getCoordinates().getLng());
            location.setCoordinates(coordinates);
        }
        event.setLocation(location);
        
        return event;
    }

    private Event convertUpdateRequestToEvent(EventUpdateRequest request) {
        Event event = new Event();
        event.setTitle(request.getTitle());
        event.setDescription(request.getDescription());
        event.setStartDate(request.getStartDate());
        event.setEndDate(request.getEndDate());
        event.setPrice(request.getPrice());
        event.setCapacity(request.getCapacity());
        event.setRegistrationDeadline(request.getRegistrationDeadline());
        event.setTags(request.getTags());
        event.setCategory(request.getCategory());
        event.setIsOnline(request.getIsOnline());
        event.setMeetingLink(request.getMeetingLink());
        
        // Set location
        Event.Location location = new Event.Location();
        location.setAddress(request.getLocation().getAddress());
        location.setCity(request.getLocation().getCity());
        location.setCountry(request.getLocation().getCountry());
        if (request.getLocation().getCoordinates() != null) {
            Event.Location.Coordinates coordinates = new Event.Location.Coordinates();
            coordinates.setLat(request.getLocation().getCoordinates().getLat());
            coordinates.setLng(request.getLocation().getCoordinates().getLng());
            location.setCoordinates(coordinates);
        }
        event.setLocation(location);
        
        return event;
    }
}
