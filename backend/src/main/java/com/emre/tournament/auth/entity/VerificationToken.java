package com.emre.tournament.auth.entity;

import com.emre.tournament.user.entity.User;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "verification_tokens", uniqueConstraints = {
        @UniqueConstraint(name = "uk_verification_token", columnNames = "token")
})
public class VerificationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String token;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_verification_token_user"))
    private User user;

    @Column(nullable = false)
    private Instant expiresAt;

    protected VerificationToken() {
    }

    public VerificationToken(String token, User user, Instant expiresAt) {
        this.token = token;
        this.user = user;
        this.expiresAt = expiresAt;
    }

    public Long getId() {
        return id;
    }

    public String getToken() {
        return token;
    }

    public User getUser() {
        return user;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }
}
