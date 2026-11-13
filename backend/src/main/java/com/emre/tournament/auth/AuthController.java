package com.emre.tournament.auth;

import com.emre.tournament.auth.dto.ForgotPasswordRequest;
import com.emre.tournament.auth.dto.LoginRequest;
import com.emre.tournament.auth.dto.LoginResponse;
import com.emre.tournament.auth.dto.RegisterRequest;
import com.emre.tournament.auth.dto.ResetPasswordRequest;
import com.emre.tournament.auth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public String register(@RequestBody @Valid RegisterRequest req) {
        return authService.register(req);
    }

    @GetMapping("/confirm")
    public String confirm(@RequestParam String token) {
        authService.confirmEmail(token);
        return "Email confirmed. You can login now.";
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody @Valid LoginRequest req) {
        return authService.login(req);
    }

    @PostMapping("/forgot-password")
    public String forgotPassword(@RequestBody @Valid ForgotPasswordRequest req) {
        return authService.forgotPassword(req);
    }

    @PostMapping("/reset-password")
    public String resetPassword(@RequestBody @Valid ResetPasswordRequest req) {
        return authService.resetPassword(req);
    }
}
