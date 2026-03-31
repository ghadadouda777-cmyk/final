package com.eventify.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "events")
@EntityListeners(AuditingEntityListener.class)
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Please add a title")
    @Size(max = 100, message = "Title cannot exceed 100 characters")
    @Column(nullable = false)
    private String title;

    @NotBlank(message = "Please add a description")
    @Size(max = 2000, message = "Description cannot exceed 2000 characters")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @NotNull(message = "Please add a starting date")
    @Column(nullable = false)
    private LocalDateTime startDate;

    @NotNull(message = "Please add an ending date")
    @Column(nullable = false)
    private LocalDateTime endDate;

    @Column(nullable = false)
    private Double price = 0.0;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "address", column = @Column(name = "location_address", nullable = false)),
        @AttributeOverride(name = "city", column = @Column(name = "location_city")),
        @AttributeOverride(name = "country", column = @Column(name = "location_country")),
        @AttributeOverride(name = "coordinates.lat", column = @Column(name = "location_lat")),
        @AttributeOverride(name = "coordinates.lng", column = @Column(name = "location_lng"))
    })
    private Location location = new Location();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organizer_id", nullable = false)
    private User organizer;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;

    @Column(columnDefinition = "TEXT")
    private String bannerImage = "default-event.jpg";

    @Min(value = 1, message = "Capacity must be at least 1")
    @Column(nullable = false)
    private Integer capacity;

    @Column(nullable = false)
    private Integer ticketsSold = 0;

    private LocalDateTime registrationDeadline;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.DRAFT;

    @ElementCollection
    @CollectionTable(name = "event_tags", joinColumns = @JoinColumn(name = "event_id"))
    @Column(name = "tag")
    private List<String> tags;

    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating cannot be more than 5")
    private Double averageRating;

    @Column(nullable = false)
    private Boolean isOnline = false;

    @Column(columnDefinition = "TEXT")
    private String meetingLink;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Registration> registrations;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Comment> comments;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Review> reviews;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Message> messages;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public enum Category {
        MUSIC_CONCERTS("Music & Concerts"),
        CONFERENCES_BUSINESS("Conferences & Business"),
        EDUCATION_CLASSES("Education & Classes"),
        WEDDINGS_CELEBRATIONS("Weddings & Celebrations"),
        SPORTS_WELLNESS("Sports & Wellness"),
        ARTS_CULTURE("Arts & Culture"),
        GASTRONOMY_FOOD("Gastronomy & Food"),
        COMMUNITY_SOCIAL("Community & Social"),
        TECHNOLOGY_GAMING("Technology & Gaming"),
        PARTIES_LEISURE("Parties & Leisure");

        private final String displayName;

        Category(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public enum Status {
        DRAFT, PUBLISHED, COMPLETED, CANCELLED
    }

    @Embeddable
    public static class Location {
        @NotBlank(message = "Please add an address")
        private String address;
        
        private String city;
        
        private String country;
        
        @Embedded
        private Coordinates coordinates = new Coordinates();

        public static class Coordinates {
            private Double lat;
            private Double lng;

            public Coordinates() {}

            public Coordinates(Double lat, Double lng) {
                this.lat = lat;
                this.lng = lng;
            }

            public Double getLat() { return lat; }
            public void setLat(Double lat) { this.lat = lat; }

            public Double getLng() { return lng; }
            public void setLng(Double lng) { this.lng = lng; }
        }

        // Getters and setters for Location
        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }

        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }

        public String getCountry() { return country; }
        public void setCountry(String country) { this.country = country; }

        public Coordinates getCoordinates() { return coordinates; }
        public void setCoordinates(Coordinates coordinates) { this.coordinates = coordinates; }
    }

    // Virtual for checking if the event is full
    public boolean isFull() {
        return ticketsSold >= capacity;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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

    public Location getLocation() { return location; }
    public void setLocation(Location location) { this.location = location; }

    public User getOrganizer() { return organizer; }
    public void setOrganizer(User organizer) { this.organizer = organizer; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }

    public String getBannerImage() { return bannerImage; }
    public void setBannerImage(String bannerImage) { this.bannerImage = bannerImage; }

    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }

    public Integer getTicketsSold() { return ticketsSold; }
    public void setTicketsSold(Integer ticketsSold) { this.ticketsSold = ticketsSold; }

    public LocalDateTime getRegistrationDeadline() { return registrationDeadline; }
    public void setRegistrationDeadline(LocalDateTime registrationDeadline) { this.registrationDeadline = registrationDeadline; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }

    public Double getAverageRating() { return averageRating; }
    public void setAverageRating(Double averageRating) { this.averageRating = averageRating; }

    public Boolean getIsOnline() { return isOnline; }
    public void setIsOnline(Boolean isOnline) { this.isOnline = isOnline; }

    public String getMeetingLink() { return meetingLink; }
    public void setMeetingLink(String meetingLink) { this.meetingLink = meetingLink; }

    public List<Registration> getRegistrations() { return registrations; }
    public void setRegistrations(List<Registration> registrations) { this.registrations = registrations; }

    public List<Comment> getComments() { return comments; }
    public void setComments(List<Comment> comments) { this.comments = comments; }

    public List<Review> getReviews() { return reviews; }
    public void setReviews(List<Review> reviews) { this.reviews = reviews; }

    public List<Message> getMessages() { return messages; }
    public void setMessages(List<Message> messages) { this.messages = messages; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
