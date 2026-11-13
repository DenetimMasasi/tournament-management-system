package com.emre.tournament.tournament.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ApplyTournamentRequest {

    @NotBlank
    @Size(max = 60)
    public String licenseNumber;

    @Min(1)
    public int ranking;
}
