package com.eventify.service;

import com.eventify.entity.Event;
import com.eventify.entity.Registration;
import com.eventify.entity.User;
import com.eventify.repository.EventRepository;
import com.eventify.repository.RegistrationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class RegistrationService {

    @Autowired
    private RegistrationRepository registrationRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private EmailService emailService;

    public Page<Registration> getAllRegistrations(Pageable pageable) {
        return registrationRepository.findAll(pageable);
    }

    public Registration getRegistrationById(Long id) {
        return registrationRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Registration not found"));
    }

    public Registration registerForEvent(Long eventId, User user) {
        Event event = eventRepository.findById(eventId)
            .orElseThrow(() -> new RuntimeException("Event not found"));

        // Check if event is published
        if (event.getStatus() != Event.Status.PUBLISHED) {
            throw new RuntimeException("Event is not available for registration");
        }

        // Check if registration deadline has passed
        if (event.getRegistrationDeadline() != null && 
            event.getRegistrationDeadline().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Registration deadline has passed");
        }

        // Check if event is full
        long registeredCount = registrationRepository.countRegisteredUsers(event);
        if (registeredCount >= event.getCapacity()) {
            throw new RuntimeException("Event is full");
        }

        // Check if user is already registered
        if (registrationRepository.findByUserAndEvent(user, event).isPresent()) {
            throw new RuntimeException("User is already registered for this event");
        }

        // Check if user is the organizer
        if (event.getOrganizer().getId().equals(user.getId())) {
            throw new RuntimeException("Organizer cannot register for their own event");
        }

        Registration registration = new Registration();
        registration.setUser(user);
        registration.setEvent(event);
        registration.setStatus(Registration.RegistrationStatus.REGISTERED);
        registration.setRegistrationDate(LocalDateTime.now());
        registration.setAmountPaid(event.getPrice());

        Registration savedRegistration = registrationRepository.save(registration);

        // Update event tickets sold
        event.setTicketsSold(event.getTicketsSold() + 1);
        eventRepository.save(event);

        // Send confirmation email
        emailService.sendRegistrationConfirmation(user, event);

        return savedRegistration;
    }

    public Registration cancelRegistration(Long registrationId, User currentUser) {
        Registration registration = getRegistrationById(registrationId);

        // Check if user owns this registration or is admin
        if (!registration.getUser().getId().equals(currentUser.getId()) && 
            !currentUser.getRole().equals(User.Role.ADMIN)) {
            throw new RuntimeException("Not authorized to cancel this registration");
        }

        // Check if event has already started
        if (registration.getEvent().getStartDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Cannot cancel registration for event that has started");
        }

        registration.setStatus(Registration.RegistrationStatus.CANCELLED);
        Registration savedRegistration = registrationRepository.save(registration);

        // Update event tickets sold
        Event event = registration.getEvent();
        event.setTicketsSold(Math.max(0, event.getTicketsSold() - 1));
        eventRepository.save(event);

        // Send cancellation email
        emailService.sendRegistrationCancellation(registration.getUser(), event);

        return savedRegistration;
    }

    public Registration markAttendance(Long registrationId, User currentUser) {
        Registration registration = getRegistrationById(registrationId);

        // Check if user is the event organizer or admin
        if (!registration.getEvent().getOrganizer().getId().equals(currentUser.getId()) && 
            !currentUser.getRole().equals(User.Role.ADMIN)) {
            throw new RuntimeException("Not authorized to mark attendance for this registration");
        }

        registration.setStatus(Registration.RegistrationStatus.ATTENDED);
        return registrationRepository.save(registration);
    }

    public Registration markNoShow(Long registrationId, User currentUser) {
        Registration registration = getRegistrationById(registrationId);

        // Check if user is the event organizer or admin
        if (!registration.getEvent().getOrganizer().getId().equals(currentUser.getId()) && 
            !currentUser.getRole().equals(User.Role.ADMIN)) {
            throw new RuntimeException("Not authorized to mark no-show for this registration");
        }

        registration.setStatus(Registration.RegistrationStatus.NO_SHOW);
        return registrationRepository.save(registration);
    }

    public List<Registration> getUserRegistrations(User user) {
        return registrationRepository.findByUser(user);
    }

    public List<Registration> getEventRegistrations(Long eventId, User currentUser) {
        Event event = eventRepository.findById(eventId)
            .orElseThrow(() -> new RuntimeException("Event not found"));

        // Check if user is the event organizer or admin
        if (!event.getOrganizer().getId().equals(currentUser.getId()) && 
            !currentUser.getRole().equals(User.Role.ADMIN)) {
            throw new RuntimeException("Not authorized to view registrations for this event");
        }

        return registrationRepository.findByEvent(event);
    }

    public List<Registration> getRegistrationsByOrganizer(User organizer) {
        return registrationRepository.findByEventOrganizer(organizer);
    }

    public List<Registration> getRegistrationsByStatus(Registration.RegistrationStatus status) {
        return registrationRepository.findByStatus(status);
    }

    public long getRegisteredUsersCount(Long eventId) {
        Event event = eventRepository.findById(eventId)
            .orElseThrow(() -> new RuntimeException("Event not found"));
        return registrationRepository.countRegisteredUsers(event);
    }

    public long getAttendedEventsCount(User user) {
        return registrationRepository.countAttendedEvents(user);
    }

    public boolean isUserRegistered(Long eventId, User user) {
        Event event = eventRepository.findById(eventId)
            .orElseThrow(() -> new RuntimeException("Event not found"));
        return registrationRepository.findByUserAndEvent(user, event).isPresent();
    }
}
