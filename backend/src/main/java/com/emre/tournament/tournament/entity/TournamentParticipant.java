package com.emre.tournament.tournament.entity;

import com.emre.tournament.user.entity.User;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "tournament_participants",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_tournament_license", columnNames = {"tournament_id", "license_number"}),
                @UniqueConstraint(name = "uk_tournament_ranking", columnNames = {"tournament_id", "ranking"})
        })
public class TournamentParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "tournament_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_participant_tournament"))
    private Tournament tournament;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_participant_user"))
    private User user;

    @Column(name = "license_number", nullable = false, length = 60)
    private String licenseNumber;

    @Column(nullable = false)
    private int ranking;

    @Column(nullable = false)
    private Instant appliedAt = Instant.now();

    public TournamentParticipant() { }

    public Long getId() { return id; }

    public Tournament getTournament() { return tournament; }
    public void setTournament(Tournament tournament) { this.tournament = tournament; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getLicenseNumber() { return licenseNumber; }
    public void setLicenseNumber(String licenseNumber) { this.licenseNumber = licenseNumber; }

    public int getRanking() { return ranking; }
    public void setRanking(int ranking) { this.ranking = ranking; }

    public Instant getAppliedAt() { return appliedAt; }
}
