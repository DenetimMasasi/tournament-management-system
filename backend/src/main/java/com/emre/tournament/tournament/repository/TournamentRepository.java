package com.emre.tournament.tournament.repository;

import com.emre.tournament.tournament.entity.Tournament;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import java.util.Optional;

public interface TournamentRepository extends JpaRepository<Tournament, Long> {

    @Query("""
            select t
            from Tournament t
            where (:q is null or :q = '' or lower(t.name) like lower(concat('%', :q, '%')))
              and (:city is null or :city = '' or lower(t.city) = lower(:city))
              and (:sportType is null or :sportType = '' or lower(t.sportType) = lower(:sportType))
              and (:status is null or :status = '' or lower(t.status) = lower(:status))
            """)
    Page<Tournament> search(
            @Param("q") String q,
            @Param("city") String city,
            @Param("sportType") String sportType,
            @Param("status") String status,
            Pageable pageable
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select t from Tournament t where t.id = :id")
    Optional<Tournament> findByIdForUpdate(@Param("id") Long id);
}
