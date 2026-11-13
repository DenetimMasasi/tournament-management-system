package com.emre.tournament.tournament.dto;

import java.time.Instant;

public class MatchResponse {

    public Long id;
    public Long tournamentId;
    public int roundNo;
    public int matchNo;

    public Long player1UserId;
    public String player1License;
    public int player1Ranking;

    public Long player2UserId;
    public String player2License;
    public Integer player2Ranking;

    public Instant createdAt;

    public String status;
    public Long player1SelectedWinnerUserId;
    public Long player2SelectedWinnerUserId;
    public Long confirmedWinnerUserId;

    public MatchResponse(
            Long id,
            Long tournamentId,
            int roundNo,
            int matchNo,
            Long player1UserId,
            String player1License,
            int player1Ranking,
            Long player2UserId,
            String player2License,
            Integer player2Ranking,
            Instant createdAt,
            String status,
            Long player1SelectedWinnerUserId,
            Long player2SelectedWinnerUserId,
            Long confirmedWinnerUserId
    ) {
        this.id = id;
        this.tournamentId = tournamentId;
        this.roundNo = roundNo;
        this.matchNo = matchNo;
        this.player1UserId = player1UserId;
        this.player1License = player1License;
        this.player1Ranking = player1Ranking;
        this.player2UserId = player2UserId;
        this.player2License = player2License;
        this.player2Ranking = player2Ranking;
        this.createdAt = createdAt;

        this.status = status;
        this.player1SelectedWinnerUserId = player1SelectedWinnerUserId;
        this.player2SelectedWinnerUserId = player2SelectedWinnerUserId;
        this.confirmedWinnerUserId = confirmedWinnerUserId;
    }
}
