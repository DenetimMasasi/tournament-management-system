package com.emre.tournament.tournament.dto;

import java.util.List;

public class SeedTournamentResponse {

    public Long tournamentId;
    public int totalParticipants;
    public int matchesCreated;
    public List<MatchResponse> matches;

    public SeedTournamentResponse(Long tournamentId, int totalParticipants, int matchesCreated, List<MatchResponse> matches) {
        this.tournamentId = tournamentId;
        this.totalParticipants = totalParticipants;
        this.matchesCreated = matchesCreated;
        this.matches = matches;
    }
}
