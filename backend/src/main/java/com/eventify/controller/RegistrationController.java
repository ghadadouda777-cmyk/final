package com.eventify.controller;

import com.eventify.entity.Registration;
import com.eventify.entity.User;
import com.eventify.service.RegistrationService;
import com.eventify.service.AuthService;
import com.eventify.dto.RegistrationStatusUpdateRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/registrations")
@CrossOrigin(origins = "${cors.allowed-origins}")
public class RegistrationController {

    @Autowired
    private RegistrationService registrationService;

    @Autowired
    private AuthService authService;

    @GetMapping
    public ResponseEntity<Page<Registration>> getAllRegistrations(Pageable pageable) {
        User currentUser = authService.getCurrentUser();
        
        // Only admins can see all registrations
        if (currentUser.getRole() != User.Role.ADMIN) {
            throw new RuntimeException("Not authorized to view all registrations");
        }
        
        Page<Registration> registrations = registrationService.getAllRegistrations(pageable);
        return ResponseEntity.ok(registrations);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Registration> getRegistrationById(@PathVariable Long id) {
        Registration registration = registrationService.getRegistrationById(id);
        User currentUser = authService.getCurrentUser();
        
        // Check if user owns this registration or is admin/organizer
        if (!registration.getUser().getId().equals(currentUser.getId()) && 
            !registration.getEvent().getOrganizer().getId().equals(currentUser.getId()) &&
            currentUser.getRole() != User.Role.ADMIN) {
            throw new RuntimeException("Not authorized to view this registration");
        }
        
        return ResponseEntity.ok(registration);
    }

    @PostMapping("/events/{eventId}")
    public ResponseEntity<Registration> registerForEvent(@PathVariable Long eventId) {
        User currentUser = authService.getCurrentUser();
        Registration registration = registrationService.registerForEvent(eventId, currentUser);
        return ResponseEntity.ok(registration);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Registration> cancelRegistration(@PathVariable Long id) {
        User currentUser = authService.getCurrentUser();
        Registration registration = registrationService.cancelRegistration(id, currentUser);
        return ResponseEntity.ok(registration);
    }

    @PutMapping("/{id}/attendance")
    public ResponseEntity<Registration> markAttendance(
            @PathVariable Long id,
            @Valid @RequestBody RegistrationStatusUpdateRequest request) {
        
        User currentUser = authService.getCurrentUser();
        
        Registration registration;
        if (request.getStatus() == Registration.RegistrationStatus.ATTENDED) {
            registration = registrationService.markAttendance(id, currentUser);
        } else if (request.getStatus() == Registration.RegistrationStatus.NO_SHOW) {
            registration = registrationService.markNoShow(id, currentUser);
        } else {
            throw new RuntimeException("Invalid status for attendance update");
        }
        
        return ResponseEntity.ok(registration);
    }

    @GetMapping("/my-registrations")
    public ResponseEntity<List<Registration>> getMyRegistrations() {
        User currentUser = authService.getCurrentUser();
        List<Registration> registrations = registrationService.getUserRegistrations(currentUser);
        return ResponseEntity.ok(registrations);
    }

    @GetMapping("/events/{eventId}")
    public ResponseEntity<List<Registration>> getEventRegistrations(@PathVariable Long eventId) {
        User currentUser = authService.getCurrentUser();
        List<Registration> registrations = registrationService.getEventRegistrations(eventId, currentUser);
        return ResponseEntity.ok(registrations);
    }

    @GetMapping("/my-organized-events")
    public ResponseEntity<List<Registration>> getRegistrationsForMyEvents() {
        User currentUser = authService.getCurrentUser();
        List<Registration> registrations = registrationService.getRegistrationsByOrganizer(currentUser);
        return ResponseEntity.ok(registrations);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Registration>> getRegistrationsByStatus(@PathVariable Registration.RegistrationStatus status) {
        User currentUser = authService.getCurrentUser();
        
        // Only admins can filter by status
        if (currentUser.getRole() != User.Role.ADMIN) {
            throw new RuntimeException("Not authorized to filter registrations by status");
        }
        
        List<Registration> registrations = registrationService.getRegistrationsByStatus(status);
        return ResponseEntity.ok(registrations);
    }

    @GetMapping("/events/{eventId}/count")
    public ResponseEntity<Long> getRegisteredUsersCount(@PathVariable Long eventId) {
        long count = registrationService.getRegisteredUsersCount(eventId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/events/{eventId}/is-registered")
    public ResponseEntity<Boolean> isUserRegistered(@PathVariable Long eventId) {
        User currentUser = authService.getCurrentUser();
        boolean isRegistered = registrationService.isUserRegistered(eventId, currentUser);
        return ResponseEntity.ok(isRegistered);
    }

    @GetMapping("/attended-count")
    public ResponseEntity<Long> getAttendedEventsCount() {
        User currentUser = authService.getCurrentUser();
        long count = registrationService.getAttendedEventsCount(currentUser);
        return ResponseEntity.ok(count);
    }
}
