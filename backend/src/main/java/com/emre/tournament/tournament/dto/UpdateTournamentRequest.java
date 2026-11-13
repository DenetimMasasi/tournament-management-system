package com.emre.tournament.tournament.dto;

import jakarta.validation.constraints.NotBlank;

public class UpdateTournamentRequest {

    @NotBlank
    public String description;

    public String sponsorLogos;
}
