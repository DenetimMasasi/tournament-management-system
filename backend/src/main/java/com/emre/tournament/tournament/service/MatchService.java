package com.emre.tournament.tournament.service;

import com.emre.tournament.tournament.dto.MatchResponse;
import com.emre.tournament.tournament.dto.SubmitMatchResultRequest;
import com.emre.tournament.tournament.entity.Match;
import com.emre.tournament.tournament.entity.TournamentParticipant;
import com.emre.tournament.tournament.repository.MatchRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MatchService {

    private final MatchRepository matchRepository;
    private final TournamentService tournamentService;

    public MatchService(MatchRepository matchRepository, TournamentService tournamentService) {
        this.matchRepository = matchRepository;
        this.tournamentService = tournamentService;
    }

    @Transactional
    public MatchResponse submitResult(Long matchId, Long currentUserId, SubmitMatchResultRequest req) {
        Match m = matchRepository.findByIdForUpdate(matchId)
                .orElseThrow(() -> new IllegalArgumentException("Match not found"));

        if ("COMPLETED".equalsIgnoreCase(m.getStatus())) {
            throw new IllegalArgumentException("Match already completed");
        }

        TournamentParticipant p1 = m.getPlayer1();
        TournamentParticipant p2 = m.getPlayer2();

        if (p1 == null || p1.getUser() == null) {
            throw new IllegalArgumentException("Match has no player1");
        }

        Long p1UserId = p1.getUser().getId();
        Long p2UserId = (p2 != null && p2.getUser() != null) ? p2.getUser().getId() : null;

        if (p2UserId == null) {
            throw new IllegalArgumentException("Cannot enter result for a BYE match");
        }

        if (!currentUserId.equals(p1UserId) && !currentUserId.equals(p2UserId)) {
            throw new IllegalArgumentException("You are not a participant of this match");
        }

        Long winnerUserId = req.winnerUserId;
        if (winnerUserId == null) {
            throw new IllegalArgumentException("winnerUserId is required");
        }
        if (!winnerUserId.equals(p1UserId) && !winnerUserId.equals(p2UserId)) {
            throw new IllegalArgumentException("Winner must be one of the match participants");
        }

        if (currentUserId.equals(p1UserId)) {
            m.setPlayer1SelectedWinnerUserId(winnerUserId);
        } else {
            m.setPlayer2SelectedWinnerUserId(winnerUserId);
        }

        // If both players entered results, confirm or reset.
        if (m.getPlayer1SelectedWinnerUserId() != null && m.getPlayer2SelectedWinnerUserId() != null) {
            if (m.getPlayer1SelectedWinnerUserId().equals(m.getPlayer2SelectedWinnerUserId())) {
                Long confirmed = m.getPlayer1SelectedWinnerUserId();
                m.setConfirmedWinnerUserId(confirmed);
                m.setStatus("COMPLETED");

                // Optional: clear temp selections after confirmation
                m.setPlayer1SelectedWinnerUserId(null);
                m.setPlayer2SelectedWinnerUserId(null);

                TournamentParticipant winner = confirmed.equals(p1UserId) ? p1 : p2;
                matchRepository.save(m);

                // Requirement 9: update ladder/next round immediately.
                tournamentService.propagateWinnerToNextRound(m.getTournament().getId(), m.getRoundNo(), m.getMatchNo(), winner);
            } else {
                // Conflicting results: withdraw both.
                m.setPlayer1SelectedWinnerUserId(null);
                m.setPlayer2SelectedWinnerUserId(null);
                m.setConfirmedWinnerUserId(null);
                m.setStatus("SCHEDULED");
                matchRepository.save(m);
            }
        } else {
            m.setStatus("PENDING_CONFIRMATION");
            matchRepository.save(m);
        }

        // Re-read without lock is fine; we already saved state.
        return tournamentService.toMatchResponsePublic(matchRepository.findById(matchId)
                .orElseThrow(() -> new IllegalArgumentException("Match not found")));
    }
}
