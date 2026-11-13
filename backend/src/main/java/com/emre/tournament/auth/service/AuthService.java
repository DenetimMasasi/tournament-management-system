package com.emre.tournament.auth.service;

import com.emre.tournament.auth.dto.ForgotPasswordRequest;
import com.emre.tournament.auth.dto.LoginRequest;
import com.emre.tournament.auth.dto.LoginResponse;
import com.emre.tournament.auth.dto.RegisterRequest;
import com.emre.tournament.auth.dto.ResetPasswordRequest;
import com.emre.tournament.auth.entity.ResetPasswordToken;
import com.emre.tournament.auth.entity.VerificationToken;
import com.emre.tournament.auth.repository.ResetPasswordTokenRepository;
import com.emre.tournament.auth.repository.VerificationTokenRepository;
import com.emre.tournament.user.entity.User;
import com.emre.tournament.user.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final ResetPasswordTokenRepository resetPasswordTokenRepository;
    private final MailService mailService;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository,
                       VerificationTokenRepository verificationTokenRepository,
                       ResetPasswordTokenRepository resetPasswordTokenRepository,
                       MailService mailService,
                       BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.verificationTokenRepository = verificationTokenRepository;
        this.resetPasswordTokenRepository = resetPasswordTokenRepository;
        this.mailService = mailService;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public String register(RegisterRequest req) {
        String email = normalizeEmail(req.email);

        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already in use");
        }

        User user = new User();
        user.setFirstName(req.firstName);
        user.setLastName(req.lastName);
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(req.password));
        user.setEnabled(false);

        user = userRepository.save(user);

        String token = UUID.randomUUID().toString();
        Instant expiresAt = Instant.now().plus(24, ChronoUnit.HOURS);

        verificationTokenRepository.save(new VerificationToken(token, user, expiresAt));
        mailService.sendConfirmationEmail(user.getEmail(), token);

        return "Registration successful. Please check your email.";
    }

    @Transactional
    public void confirmEmail(String token) {
        VerificationToken vt = verificationTokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid token"));

        if (vt.getExpiresAt().isBefore(Instant.now())) {
            throw new IllegalArgumentException("Token expired");
        }

        User user = vt.getUser();
        user.setEnabled(true);
        userRepository.save(user);

        verificationTokenRepository.delete(vt);
    }

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest req) {
        String email = normalizeEmail(req.email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));

        if (!user.isEnabled()) {
            throw new IllegalArgumentException("Please confirm your email first");
        }

        if (!passwordEncoder.matches(req.password, user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid credentials");
        }

        return new LoginResponse(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName()
        );
    }

    @Transactional
    public String forgotPassword(ForgotPasswordRequest req) {
        String email = normalizeEmail(req.email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!user.isEnabled()) {
            throw new IllegalArgumentException("Please confirm your email first");
        }

        String token = UUID.randomUUID().toString();
        Instant expiresAt = Instant.now().plus(24, ChronoUnit.HOURS);

        resetPasswordTokenRepository.save(new ResetPasswordToken(token, user, expiresAt));
        mailService.sendPasswordResetEmail(user.getEmail(), token);

        return "Password reset email sent.";
    }

    @Transactional
    public String resetPassword(ResetPasswordRequest req) {
        ResetPasswordToken rt = resetPasswordTokenRepository.findByToken(req.token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid token"));

        if (rt.getExpiresAt().isBefore(Instant.now())) {
            throw new IllegalArgumentException("Token expired");
        }

        User user = rt.getUser();
        user.setPasswordHash(passwordEncoder.encode(req.newPassword));
        userRepository.save(user);

        resetPasswordTokenRepository.delete(rt);
        return "Password updated successfully.";
    }

    private String normalizeEmail(String email) {
        if (email == null) return null;
        return email.trim().toLowerCase();
    }
}
