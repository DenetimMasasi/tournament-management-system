package com.emre.tournament.tournament.dto;

import java.time.Instant;

public class ApplyTournamentResponse {

    public Long tournamentId;
    public Long userId;
    public String licenseNumber;
    public int ranking;
    public Instant appliedAt;

    public ApplyTournamentResponse(Long tournamentId, Long userId, String licenseNumber, int ranking, Instant appliedAt) {
        this.tournamentId = tournamentId;
        this.userId = userId;
        this.licenseNumber = licenseNumber;
        this.ranking = ranking;
        this.appliedAt = appliedAt;
    }
}
