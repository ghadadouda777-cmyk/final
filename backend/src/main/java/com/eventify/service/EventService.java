package com.eventify.service;

import com.eventify.entity.Event;
import com.eventify.entity.User;
import com.eventify.repository.EventRepository;
import com.eventify.repository.RegistrationRepository;
import com.eventify.service.storage.CloudinaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private RegistrationRepository registrationRepository;

    @Autowired
    private CloudinaryService cloudinaryService;

    public Page<Event> getAllEvents(Pageable pageable) {
        return eventRepository.findAll(pageable);
    }

    public Page<Event> getPublishedEvents(Pageable pageable) {
        return eventRepository.findAll(pageable);
    }

    public Event getEventById(Long id) {
        return eventRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Event not found"));
    }

    public Event createEvent(Event event, MultipartFile bannerImage, User organizer) {
        event.setOrganizer(organizer);
        event.setStatus(Event.Status.DRAFT);
        
        if (bannerImage != null && !bannerImage.isEmpty()) {
            try {
                String imageUrl = cloudinaryService.uploadImage(bannerImage);
                event.setBannerImage(imageUrl);
            } catch (IOException e) {
                throw new RuntimeException("Failed to upload banner image", e);
            }
        }

        return eventRepository.save(event);
    }

    public Event updateEvent(Long id, Event eventDetails, MultipartFile bannerImage, User currentUser) {
        Event event = getEventById(id);

        // Check if user is the organizer or admin
        if (!event.getOrganizer().getId().equals(currentUser.getId()) && 
            !currentUser.getRole().equals(User.Role.ADMIN)) {
            throw new RuntimeException("Not authorized to update this event");
        }

        // Update fields
        event.setTitle(eventDetails.getTitle());
        event.setDescription(eventDetails.getDescription());
        event.setStartDate(eventDetails.getStartDate());
        event.setEndDate(eventDetails.getEndDate());
        event.setPrice(eventDetails.getPrice());
        event.setLocation(eventDetails.getLocation());
        event.setCategory(eventDetails.getCategory());
        event.setCapacity(eventDetails.getCapacity());
        event.setRegistrationDeadline(eventDetails.getRegistrationDeadline());
        event.setTags(eventDetails.getTags());
        event.setIsOnline(eventDetails.getIsOnline());
        event.setMeetingLink(eventDetails.getMeetingLink());

        if (bannerImage != null && !bannerImage.isEmpty()) {
            try {
                String imageUrl = cloudinaryService.uploadImage(bannerImage);
                event.setBannerImage(imageUrl);
            } catch (IOException e) {
                throw new RuntimeException("Failed to upload banner image", e);
            }
        }

        return eventRepository.save(event);
    }

    public void deleteEvent(Long id, User currentUser) {
        Event event = getEventById(id);

        // Check if user is the organizer or admin
        if (!event.getOrganizer().getId().equals(currentUser.getId()) && 
            !currentUser.getRole().equals(User.Role.ADMIN)) {
            throw new RuntimeException("Not authorized to delete this event");
        }

        // Check if there are registrations
        long registrationCount = registrationRepository.countRegisteredUsers(event);
        if (registrationCount > 0) {
            throw new RuntimeException("Cannot delete event with registered users");
        }

        eventRepository.delete(event);
    }

    public Event publishEvent(Long id, User currentUser) {
        Event event = getEventById(id);

        // Check if user is the organizer or admin
        if (!event.getOrganizer().getId().equals(currentUser.getId()) && 
            !currentUser.getRole().equals(User.Role.ADMIN)) {
            throw new RuntimeException("Not authorized to publish this event");
        }

        // Validate event before publishing
        validateEventForPublishing(event);

        event.setStatus(Event.Status.PUBLISHED);
        return eventRepository.save(event);
    }

    public Event cancelEvent(Long id, User currentUser) {
        Event event = getEventById(id);

        // Check if user is the organizer or admin
        if (!event.getOrganizer().getId().equals(currentUser.getId()) && 
            !currentUser.getRole().equals(User.Role.ADMIN)) {
            throw new RuntimeException("Not authorized to cancel this event");
        }

        event.setStatus(Event.Status.CANCELLED);
        return eventRepository.save(event);
    }

    public List<Event> getEventsByOrganizer(User organizer) {
        return eventRepository.findByOrganizer(organizer);
    }

    public List<Event> getUpcomingEvents() {
        return eventRepository.findUpcomingEvents(LocalDateTime.now());
    }

    public List<Event> searchEvents(String keyword) {
        return eventRepository.searchEvents(keyword);
    }

    public List<Event> getEventsByCategory(Event.Category category) {
        return eventRepository.findByCategory(category);
    }

    public List<Event> getOnlineEvents() {
        return eventRepository.findOnlineEvents();
    }

    public List<Event> getEventsByCity(String city) {
        return eventRepository.findByCity(city);
    }

    public List<Event> getEventsBetweenDates(LocalDateTime startDate, LocalDateTime endDate) {
        return eventRepository.findEventsBetweenDates(startDate, endDate);
    }

    public List<Event> getEventsByTags(List<String> tags) {
        return eventRepository.findByTags(tags);
    }

    public long getEventCountByOrganizerAndStatus(User organizer, Event.Status status) {
        return eventRepository.countByOrganizer(organizer);
    }

    private void validateEventForPublishing(Event event) {
        if (event.getTitle() == null || event.getTitle().trim().isEmpty()) {
            throw new RuntimeException("Event title is required");
        }
        if (event.getDescription() == null || event.getDescription().trim().isEmpty()) {
            throw new RuntimeException("Event description is required");
        }
        if (event.getStartDate() == null || event.getEndDate() == null) {
            throw new RuntimeException("Event dates are required");
        }
        if (event.getStartDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Event start date cannot be in the past");
        }
        if (event.getEndDate().isBefore(event.getStartDate())) {
            throw new RuntimeException("Event end date must be after start date");
        }
        if (event.getCapacity() == null || event.getCapacity() <= 0) {
            throw new RuntimeException("Event capacity must be greater than 0");
        }
        if (event.getCategory() == null) {
            throw new RuntimeException("Event category is required");
        }
        if (event.getLocation() == null || event.getLocation().getAddress() == null || 
            event.getLocation().getAddress().trim().isEmpty()) {
            throw new RuntimeException("Event location is required");
        }
        if (event.getIsOnline() && (event.getMeetingLink() == null || event.getMeetingLink().trim().isEmpty())) {
            throw new RuntimeException("Meeting link is required for online events");
        }
    }
}
