package com.emre.tournament.tournament.repository;

import com.emre.tournament.tournament.entity.Match;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;

import java.util.List;
import java.util.Optional;

public interface MatchRepository extends JpaRepository<Match, Long> {

    boolean existsByTournamentId(Long tournamentId);

    List<Match> findByTournamentIdOrderByRoundNoAscMatchNoAsc(Long tournamentId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select m from Match m where m.id = :id")
    Optional<Match> findByIdForUpdate(@Param("id") Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select m from Match m
            where m.tournament.id = :tournamentId
              and m.roundNo = :roundNo
              and m.matchNo = :matchNo
            """)
    Optional<Match> findByTournamentIdAndRoundNoAndMatchNoForUpdate(
            @Param("tournamentId") Long tournamentId,
            @Param("roundNo") int roundNo,
            @Param("matchNo") int matchNo
    );

    @Query("select coalesce(max(m.roundNo), 0) from Match m where m.tournament.id = :tournamentId")
    int findMaxRoundNo(@Param("tournamentId") Long tournamentId);

    @Query("""
            select m
            from Match m
            where (m.player1.user.id = :userId or (m.player2 is not null and m.player2.user.id = :userId))
              and lower(m.tournament.status) <> 'finished'
            order by m.tournament.startDate asc, m.roundNo asc, m.matchNo asc
            """)
    List<Match> findUpcomingByUserId(@Param("userId") Long userId);
}
