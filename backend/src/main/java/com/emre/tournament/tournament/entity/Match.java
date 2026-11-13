package com.emre.tournament.tournament.entity;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "tournament_matches",
        indexes = {
                @Index(name = "ix_match_tournament", columnList = "tournament_id"),
                @Index(name = "ix_match_p1", columnList = "player1_id"),
                @Index(name = "ix_match_p2", columnList = "player2_id")
        })
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "tournament_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_match_tournament"))
    private Tournament tournament;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player1_id",
            foreignKey = @ForeignKey(name = "fk_match_player1"))
    private TournamentParticipant player1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player2_id",
            foreignKey = @ForeignKey(name = "fk_match_player2"))
    private TournamentParticipant player2;

    @Column(nullable = false)
    private int roundNo;

    @Column(nullable = false)
    private int matchNo;

    @Column(nullable = false, length = 32)
    private String status = "SCHEDULED";

    // Result entry: both players must confirm the same winner.
    @Column
    private Long player1SelectedWinnerUserId;

    @Column
    private Long player2SelectedWinnerUserId;

    @Column
    private Long confirmedWinnerUserId;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    public Match() { }

    public Long getId() { return id; }

    public Tournament getTournament() { return tournament; }
    public void setTournament(Tournament tournament) { this.tournament = tournament; }

    public TournamentParticipant getPlayer1() { return player1; }
    public void setPlayer1(TournamentParticipant player1) { this.player1 = player1; }

    public TournamentParticipant getPlayer2() { return player2; }
    public void setPlayer2(TournamentParticipant player2) { this.player2 = player2; }

    public int getRoundNo() { return roundNo; }
    public void setRoundNo(int roundNo) { this.roundNo = roundNo; }

    public int getMatchNo() { return matchNo; }
    public void setMatchNo(int matchNo) { this.matchNo = matchNo; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Long getPlayer1SelectedWinnerUserId() { return player1SelectedWinnerUserId; }
    public void setPlayer1SelectedWinnerUserId(Long player1SelectedWinnerUserId) { this.player1SelectedWinnerUserId = player1SelectedWinnerUserId; }

    public Long getPlayer2SelectedWinnerUserId() { return player2SelectedWinnerUserId; }
    public void setPlayer2SelectedWinnerUserId(Long player2SelectedWinnerUserId) { this.player2SelectedWinnerUserId = player2SelectedWinnerUserId; }

    public Long getConfirmedWinnerUserId() { return confirmedWinnerUserId; }
    public void setConfirmedWinnerUserId(Long confirmedWinnerUserId) { this.confirmedWinnerUserId = confirmedWinnerUserId; }

    public Instant getCreatedAt() { return createdAt; }
}
