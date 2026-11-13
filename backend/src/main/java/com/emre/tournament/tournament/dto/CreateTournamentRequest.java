package com.emre.tournament.tournament.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CreateTournamentRequest {

    @NotBlank
    @Size(max = 140)
    public String name;

    @NotBlank
    @Size(max = 140)
    public String city;

    @NotNull
    public LocalDate startDate;

    @NotNull
    public LocalDate endDate;


    @NotNull
    public LocalDate applicationDeadline;

    @Min(2)
    @Max(1024)
    public int maxTeams;

    @NotBlank
    @Size(max = 30)
    public String sportType;

   
    @Size(max = 2000)
    public String sponsorLogos; 

   
    @Min(0)
    @Max(1024)
    public int rankedPlayers;

    @Size(max = 2000)
    public String description;
}
