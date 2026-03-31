package com.eventify.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "users")
@EntityListeners(AuditingEntityListener.class)
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Please provide a full name")
    @Column(nullable = false)
    private String fullName;

    @Email(message = "Please provide a valid email address")
    @NotBlank(message = "Please provide an email address")
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank(message = "Please provide a password")
    @Size(min = 6, message = "Password must be at least 6 characters")
    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.USER;

    @Column(nullable = false)
    private boolean isVerified = false;

    @Column(columnDefinition = "TEXT")
    private String profilePicture = "https://api.dicebear.com/7.x/avataaars/svg?seed=Lucky";

    @Size(max = 500, message = "Bio cannot exceed 500 characters")
    @Column(length = 500)
    private String bio;

    private String phone;
    private String address;
    private String website;
    private String twitter;
    private String linkedin;
    private String github;
    private boolean showProfile = true;
    private boolean showActivity = true;
    private boolean notificationsEnabled = true;
    private boolean darkMode = false;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "user_registered_events",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "event_id")
    )
    private List<Event> registeredEvents;

    @OneToMany(mappedBy = "organizer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Event> createdEvents;

    @Column(unique = true)
    private String passwordResetToken;
    
    @Column(name = "password_reset_expire")
    private LocalDateTime passwordResetExpire;
    
    @Column(unique = true)
    private String emailVerificationToken;

    @Column(nullable = false)
    private Double balance = 0.0;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public enum Role {
        USER, PARTICIPANT, ORGANIZER, ADMIN
    }

    @Embeddable
    public static class ContactInfo {
        private String phone;
        private String address;
    }

    @Embeddable
    public static class SocialLinks {
        private String website;
        private String twitter;
        private String linkedin;
        private String github;
    }

    @Embeddable
    public static class PrivacySettings {
        private boolean showProfile = true;
        private boolean showActivity = true;
    }

    @Embeddable
    public static class Preferences {
        @Column(name = "notifications_enabled")
        private boolean notificationsEnabled = true;
        
        @Column(name = "dark_mode")
        private boolean darkMode = false;
    }

    // UserDetails implementation
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + role.name()));
        return authorities;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public boolean isVerified() { return isVerified; }
    public void setVerified(boolean verified) { isVerified = verified; }

    public String getProfilePicture() { return profilePicture; }
    public void setProfilePicture(String profilePicture) { this.profilePicture = profilePicture; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }

    public String getTwitter() { return twitter; }
    public void setTwitter(String twitter) { this.twitter = twitter; }

    public String getLinkedin() { return linkedin; }
    public void setLinkedin(String linkedin) { this.linkedin = linkedin; }

    public String getGithub() { return github; }
    public void setGithub(String github) { this.github = github; }

    public boolean isShowProfile() { return showProfile; }
    public void setShowProfile(boolean showProfile) { this.showProfile = showProfile; }

    public boolean isShowActivity() { return showActivity; }
    public void setShowActivity(boolean showActivity) { this.showActivity = showActivity; }

    public boolean isNotificationsEnabled() { return notificationsEnabled; }
    public void setNotificationsEnabled(boolean notificationsEnabled) { this.notificationsEnabled = notificationsEnabled; }

    public boolean isDarkMode() { return darkMode; }
    public void setDarkMode(boolean darkMode) { this.darkMode = darkMode; }

    public List<Event> getRegisteredEvents() { return registeredEvents; }
    public void setRegisteredEvents(List<Event> registeredEvents) { this.registeredEvents = registeredEvents; }

    public List<Event> getCreatedEvents() { return createdEvents; }
    public void setCreatedEvents(List<Event> createdEvents) { this.createdEvents = createdEvents; }

    public String getPasswordResetToken() { return passwordResetToken; }
    public void setPasswordResetToken(String passwordResetToken) { this.passwordResetToken = passwordResetToken; }

    public LocalDateTime getPasswordResetExpire() { return passwordResetExpire; }
    public void setPasswordResetExpire(LocalDateTime passwordResetExpire) { this.passwordResetExpire = passwordResetExpire; }

    public String getEmailVerificationToken() { return emailVerificationToken; }
    public void setEmailVerificationToken(String emailVerificationToken) { this.emailVerificationToken = emailVerificationToken; }

    public Double getBalance() { return balance; }
    public void setBalance(Double balance) { this.balance = balance; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
