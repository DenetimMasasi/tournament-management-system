package com.emre.tournament.tournament.dto;

import java.time.Instant;
import java.time.LocalDate;

public class TournamentResponse {

    public Long id;
    public String name;
    public String city;
    public LocalDate startDate;
    public LocalDate endDate;
    public LocalDate applicationDeadline;
    public int maxTeams;
    public String sportType;
    public String status;
    public String sponsorLogos;
    public int rankedPlayers;
    public String description;
    public Instant createdAt;

 
    public String googleMapsUrl;

    public TournamentResponse(Long id,
                             String name,
                             String city,
                             LocalDate startDate,
                             LocalDate endDate,
                             LocalDate applicationDeadline,
                             int maxTeams,
                             String sportType,
                             String status,
                             String sponsorLogos,
                             int rankedPlayers,
                             String description,
                             Instant createdAt,
                             String googleMapsUrl) {
        this.id = id;
        this.name = name;
        this.city = city;
        this.startDate = startDate;
        this.endDate = endDate;
        this.applicationDeadline = applicationDeadline;
        this.maxTeams = maxTeams;
        this.sportType = sportType;
        this.status = status;
        this.sponsorLogos = sponsorLogos;
        this.rankedPlayers = rankedPlayers;
        this.description = description;
        this.createdAt = createdAt;
        this.googleMapsUrl = googleMapsUrl;
    }
}
