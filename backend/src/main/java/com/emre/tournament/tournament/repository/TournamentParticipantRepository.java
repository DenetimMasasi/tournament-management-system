package com.emre.tournament.tournament.repository;

import com.emre.tournament.tournament.entity.TournamentParticipant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TournamentParticipantRepository extends JpaRepository<TournamentParticipant, Long> {

    long countByTournamentId(Long tournamentId);

    boolean existsByTournamentIdAndUserId(Long tournamentId, Long userId);

    boolean existsByTournamentIdAndLicenseNumberIgnoreCase(Long tournamentId, String licenseNumber);

    boolean existsByTournamentIdAndRanking(Long tournamentId, int ranking);

    List<TournamentParticipant> findByUserId(Long userId);

    List<TournamentParticipant> findByTournamentIdOrderByRankingAsc(Long tournamentId);
}
