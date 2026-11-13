package com.emre.tournament.tournament.entity;

import java.time.Instant;
import java.time.LocalDate;

import com.emre.tournament.user.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "tournaments")
public class Tournament {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "organizer_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_tournament_organizer"))
    private User organizer;

    @Column(nullable = false, length = 140)
    private String name;

    @Column(nullable = false, length = 140)
    private String city;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Column(nullable = false)
    private LocalDate applicationDeadline;

    @Column(nullable = false)
    private int maxTeams;

    @Column(nullable = false, length = 30)
    private String sportType;

    @Column(nullable = false, length = 30)
    private String status;

    @Column(length = 2000)
    private String sponsorLogos;

    @Column(nullable = false)
    private int rankedPlayers;

    @Column(length = 2000)
    private String description;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    public Tournament() {}

    public Long getId() { return id; }

    public User getOrganizer() { return organizer; }
    public void setOrganizer(User organizer) { this.organizer = organizer; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public LocalDate getApplicationDeadline() { return applicationDeadline; }
    public void setApplicationDeadline(LocalDate applicationDeadline) { this.applicationDeadline = applicationDeadline; }

    public int getMaxTeams() { return maxTeams; }
    public void setMaxTeams(int maxTeams) { this.maxTeams = maxTeams; }

    public String getSportType() { return sportType; }
    public void setSportType(String sportType) { this.sportType = sportType; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getSponsorLogos() { return sponsorLogos; }
    public void setSponsorLogos(String sponsorLogos) { this.sponsorLogos = sponsorLogos; }

    public int getRankedPlayers() { return rankedPlayers; }
    public void setRankedPlayers(int rankedPlayers) { this.rankedPlayers = rankedPlayers; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Instant getCreatedAt() { return createdAt; }
}
