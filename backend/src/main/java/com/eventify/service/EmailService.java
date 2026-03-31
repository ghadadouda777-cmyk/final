package com.eventify.service;

import com.eventify.entity.Event;
import com.eventify.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendVerificationEmail(User user) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(user.getEmail());
        message.setSubject("Verify your email address");
        message.setText("Dear " + user.getFullName() + ",\n\n" +
            "Please click the following link to verify your email address:\n" +
            "http://localhost:4200/verify-email?token=" + user.getEmailVerificationToken() + "\n\n" +
            "Thank you for using Eventify!\n" +
            "Best regards,\n" +
            "The Eventify Team");
        
        mailSender.send(message);
    }

    public void sendPasswordResetEmail(User user, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(user.getEmail());
        message.setSubject("Reset your password");
        message.setText("Dear " + user.getFullName() + ",\n\n" +
            "Please click the following link to reset your password:\n" +
            "http://localhost:4200/reset-password?token=" + token + "\n\n" +
            "This link will expire in 24 hours.\n\n" +
            "If you didn't request this password reset, please ignore this email.\n\n" +
            "Best regards,\n" +
            "The Eventify Team");
        
        mailSender.send(message);
    }

    public void sendRegistrationConfirmation(User user, Event event) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(user.getEmail());
        message.setSubject("Registration confirmed for " + event.getTitle());
        message.setText("Dear " + user.getFullName() + ",\n\n" +
            "Your registration for the event '" + event.getTitle() + "' has been confirmed!\n\n" +
            "Event Details:\n" +
            "Date: " + event.getStartDate() + "\n" +
            "Location: " + event.getLocation().getAddress() + "\n" +
            "Price: $" + event.getPrice() + "\n\n" +
            "We look forward to seeing you there!\n\n" +
            "Best regards,\n" +
            "The Eventify Team");
        
        mailSender.send(message);
    }

    public void sendRegistrationCancellation(User user, Event event) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(user.getEmail());
        message.setSubject("Registration cancelled for " + event.getTitle());
        message.setText("Dear " + user.getFullName() + ",\n\n" +
            "Your registration for the event '" + event.getTitle() + "' has been cancelled.\n\n" +
            "If you didn't cancel this registration, please contact our support team.\n\n" +
            "Best regards,\n" +
            "The Eventify Team");
        
        mailSender.send(message);
    }

    public void sendEventReminder(User user, Event event) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(user.getEmail());
        message.setSubject("Reminder: " + event.getTitle() + " is starting soon!");
        message.setText("Dear " + user.getFullName() + ",\n\n" +
            "This is a friendly reminder that the event '" + event.getTitle() + "' is starting soon!\n\n" +
            "Event Details:\n" +
            "Date: " + event.getStartDate() + "\n" +
            "Location: " + event.getLocation().getAddress() + "\n" +
            (event.getIsOnline() ? "Meeting Link: " + event.getMeetingLink() + "\n" : "") +
            "\nWe look forward to seeing you there!\n\n" +
            "Best regards,\n" +
            "The Eventify Team");
        
        mailSender.send(message);
    }

    public void sendEventUpdate(User user, Event event, String updateMessage) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(user.getEmail());
        message.setSubject("Update: " + event.getTitle());
        message.setText("Dear " + user.getFullName() + ",\n\n" +
            "There is an update for the event '" + event.getTitle() + "':\n\n" +
            updateMessage + "\n\n" +
            "Best regards,\n" +
            "The Eventify Team");
        
        mailSender.send(message);
    }
}
