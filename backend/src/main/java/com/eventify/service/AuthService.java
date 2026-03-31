package com.eventify.service;

import com.eventify.entity.User;
import com.eventify.repository.UserRepository;
import com.eventify.security.JwtTokenProvider;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@Service
@Transactional
public class AuthService implements UserDetailsService {

    @Lazy
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider tokenProvider;

    // @Autowired
    // private EmailService emailService; // Désactivé pour le développement

    public String login(String email, String password) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(email, password)
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        return tokenProvider.generateToken(authentication);
    }

    public User register(String fullName, String email, String password, String role) {
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email is already registered");
        }

        User user = new User();
        user.setFullName(fullName);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        
        // Convertir string role en User.Role enum
        User.Role userRole;
        try {
            userRole = User.Role.valueOf(role.toUpperCase());
        } catch (Exception e) {
            userRole = User.Role.USER; // Valeur par défaut
        }
        user.setRole(userRole);
        
        user.setVerified(true); // Pour le développement, pas besoin de vérification email
        user.setEmailVerificationToken(null); // Pas de token pour le développement

        User savedUser = userRepository.save(user);

        // Pour le développement, on envoie pas l'email de vérification
        // try {
        //     emailService.sendVerificationEmail(savedUser);
        // } catch (Exception e) {
        //     // Ignorer les erreurs d'email pour le développement
        //     System.out.println("Email service disabled for development");
        // }

        return savedUser;
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            return (User) authentication.getPrincipal();
        }
        throw new RuntimeException("User not authenticated");
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }

    public boolean verifyEmail(String token) {
        User user = userRepository.findByEmailVerificationToken(token)
            .orElseThrow(() -> new RuntimeException("Invalid verification token"));

        user.setVerified(true);
        user.setEmailVerificationToken(null);
        userRepository.save(user);

        return true;
    }

    public String createPasswordResetToken(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));

        String token = UUID.randomUUID().toString();
        user.setPasswordResetToken(token);
        user.setPasswordResetExpire(LocalDateTime.now().plusHours(24));
        userRepository.save(user);

        // Pour le développement, on envoie pas l'email de reset
        // emailService.sendPasswordResetEmail(user, token);
        System.out.println("Password reset token created (development mode): " + token);

        return token;
    }

    public boolean resetPassword(String token, String newPassword) {
        User user = userRepository.findByPasswordResetToken(token)
            .orElseThrow(() -> new RuntimeException("Invalid reset token"));

        if (user.getPasswordResetExpire().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Reset token has expired");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setPasswordResetToken(null);
        user.setPasswordResetExpire(null);
        userRepository.save(user);

        return true;
    }

    public String refreshToken(String token) {
        if (tokenProvider.validateToken(token)) {
            Claims claims = tokenProvider.getClaimsFromToken(token);
            String email = claims.getSubject();
            
            User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

            // Create new authentication
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                user, null, user.getAuthorities()
            );

            return tokenProvider.generateToken(authentication);
        }
        throw new RuntimeException("Invalid token");
    }
}
