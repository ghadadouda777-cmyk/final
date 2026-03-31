package com.eventify.controller;

import com.eventify.entity.User;
import com.eventify.service.AuthService;
import com.eventify.dto.LoginRequest;
import com.eventify.dto.RegisterRequest;
import com.eventify.dto.AuthResponse;
import com.eventify.dto.PasswordResetRequest;
import com.eventify.dto.PasswordResetConfirmRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "${cors.allowed-origins}")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        try {
            System.out.println("=== DEBUG REGISTRATION ===");
            System.out.println("Request received: " + request);
            System.out.println("Full Name: " + request.getFullName());
            System.out.println("Email: " + request.getEmail());
            System.out.println("Password length: " + (request.getPassword() != null ? request.getPassword().length() : "null"));
            System.out.println("Role: " + request.getRole());
            
            User user = authService.register(
                request.getFullName(),
                request.getEmail(),
                request.getPassword(),
                request.getRole()
            );

            // Pour le développement, on génère directement un token sans vérification email
            String token = authService.login(request.getEmail(), request.getPassword());

            AuthResponse response = new AuthResponse();
            response.setToken(token);
            response.setMessage("Registration successful");
            response.setUserId(user.getId());
            response.setEmail(user.getEmail());
            response.setFullName(user.getFullName());
            response.setRole(user.getRole().name());
            response.setVerified(true); // Pour le développement

            System.out.println("Registration successful for user: " + user.getEmail());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("=== REGISTRATION ERROR ===");
            System.err.println("Error type: " + e.getClass().getSimpleName());
            System.err.println("Error message: " + e.getMessage());
            e.printStackTrace();
            
            AuthResponse errorResponse = new AuthResponse();
            errorResponse.setMessage("Registration failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        String token = authService.login(request.getEmail(), request.getPassword());
        User user = authService.getCurrentUser();

        AuthResponse response = new AuthResponse();
        response.setToken(token);
        response.setMessage("Login successful");
        response.setUserId(user.getId());
        response.setEmail(user.getEmail());
        response.setFullName(user.getFullName());
        response.setRole(user.getRole().name());
        response.setVerified(user.isVerified());
        response.setProfilePicture(user.getProfilePicture());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@RequestHeader("Authorization") String token) {
        String cleanToken = token.replace("Bearer ", "");
        String newToken = authService.refreshToken(cleanToken);
        User user = authService.getCurrentUser();

        AuthResponse response = new AuthResponse();
        response.setToken(newToken);
        response.setMessage("Token refreshed successfully");
        response.setUserId(user.getId());
        response.setEmail(user.getEmail());
        response.setFullName(user.getFullName());
        response.setRole(user.getRole().name());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<AuthResponse> getCurrentUser() {
        User user = authService.getCurrentUser();

        AuthResponse response = new AuthResponse();
        response.setUserId(user.getId());
        response.setEmail(user.getEmail());
        response.setFullName(user.getFullName());
        response.setRole(user.getRole().name());
        response.setVerified(user.isVerified());
        response.setProfilePicture(user.getProfilePicture());
        response.setBio(user.getBio());
        response.setBalance(user.getBalance());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify-email")
    public ResponseEntity<AuthResponse> verifyEmail(@RequestParam String token) {
        boolean verified = authService.verifyEmail(token);
        
        AuthResponse response = new AuthResponse();
        response.setMessage(verified ? "Email verified successfully" : "Invalid verification token");
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<AuthResponse> forgotPassword(@RequestBody PasswordResetRequest request) {
        String token = authService.createPasswordResetToken(request.getEmail());
        
        AuthResponse response = new AuthResponse();
        response.setMessage("Password reset link sent to your email");
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<AuthResponse> resetPassword(@Valid @RequestBody PasswordResetConfirmRequest request) {
        boolean reset = authService.resetPassword(request.getToken(), request.getNewPassword());
        
        AuthResponse response = new AuthResponse();
        response.setMessage(reset ? "Password reset successful" : "Invalid or expired reset token");
        
        return ResponseEntity.ok(response);
    }
}
