package com.emre.tournament.tournament.service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.emre.tournament.tournament.dto.ApplyTournamentRequest;
import com.emre.tournament.tournament.dto.ApplyTournamentResponse;
import com.emre.tournament.tournament.dto.CreateTournamentRequest;
import com.emre.tournament.tournament.dto.MatchResponse;
import com.emre.tournament.tournament.dto.SeedTournamentResponse;
import com.emre.tournament.tournament.dto.TournamentResponse;
import com.emre.tournament.tournament.dto.UpdateTournamentRequest;
import com.emre.tournament.tournament.entity.Match;
import com.emre.tournament.tournament.entity.Tournament;
import com.emre.tournament.tournament.entity.TournamentParticipant;
import com.emre.tournament.tournament.repository.MatchRepository;
import com.emre.tournament.tournament.repository.TournamentParticipantRepository;
import com.emre.tournament.tournament.repository.TournamentRepository;
import com.emre.tournament.user.entity.User;
import com.emre.tournament.user.repository.UserRepository;

@Service
public class TournamentService {

    private final TournamentRepository tournamentRepository;
    private final TournamentParticipantRepository participantRepository;
    private final MatchRepository matchRepository;
    private final UserRepository userRepository;

    public TournamentService(TournamentRepository tournamentRepository,
                             TournamentParticipantRepository participantRepository,
                             MatchRepository matchRepository,
                             UserRepository userRepository) {
        this.tournamentRepository = tournamentRepository;
        this.participantRepository = participantRepository;
        this.matchRepository = matchRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public TournamentResponse create(CreateTournamentRequest req, Long organizerUserId) {
        LocalDate today = LocalDate.now();

        if (req.startDate.isBefore(today)) {
            throw new IllegalArgumentException("startDate cannot be in the past");
        }
        if (req.endDate.isBefore(req.startDate)) {
            throw new IllegalArgumentException("endDate cannot be before startDate");
        }
        if (req.applicationDeadline.isAfter(req.startDate)) {
            throw new IllegalArgumentException("applicationDeadline cannot be after startDate");
        }

        User organizer = userRepository.findById(organizerUserId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!organizer.isEnabled()) {
            throw new IllegalArgumentException("User is not enabled");
        }

        Tournament t = new Tournament();
        t.setOrganizer(organizer);
        t.setName(req.name);
        t.setCity(req.city);
        t.setStartDate(req.startDate);
        t.setEndDate(req.endDate);
        t.setApplicationDeadline(req.applicationDeadline);
        t.setMaxTeams(req.maxTeams);
        t.setSportType(req.sportType);
        t.setSponsorLogos(req.sponsorLogos);
        t.setRankedPlayers(req.rankedPlayers);
        t.setDescription(req.description);
        t.setStatus(calcStatus(req.startDate, req.endDate));

        return toResponse(tournamentRepository.save(t));
    }

    @Transactional
    public TournamentResponse update(Long tournamentId, Long currentUserId, UpdateTournamentRequest req) {
        Tournament t = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new IllegalArgumentException("Tournament not found"));

        if (t.getOrganizer() == null || t.getOrganizer().getId() == null) {
            throw new IllegalArgumentException("Tournament organizer not set");
        }

        if (!t.getOrganizer().getId().equals(currentUserId)) {
            throw new IllegalArgumentException("Only organizer can edit this tournament");
        }

        t.setDescription(req.description);
        t.setSponsorLogos(req.sponsorLogos);

        return toResponse(tournamentRepository.save(t));
    }

    @Transactional(readOnly = true)
    public Page<TournamentResponse> list(String q, String city, String sportType, String status, Pageable pageable) {
        return tournamentRepository
                .search(trimToNull(q), trimToNull(city), trimToNull(sportType), trimToNull(status), pageable)
                .map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<TournamentResponse> upcoming(String q, String city, String sportType, Pageable pageable) {
        return tournamentRepository
                .search(trimToNull(q), trimToNull(city), trimToNull(sportType), "UPCOMING", pageable)
                .map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public TournamentResponse getById(Long id) {
        Tournament t = tournamentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Tournament not found"));
        return toResponse(t);
    }

    @Transactional
    public ApplyTournamentResponse apply(Long tournamentId, Long userId, ApplyTournamentRequest req) {
        Tournament t = tournamentRepository.findByIdForUpdate(tournamentId)
                .orElseThrow(() -> new IllegalArgumentException("Tournament not found"));

        User u = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!u.isEnabled()) {
            throw new IllegalArgumentException("User is not enabled");
        }

        if (LocalDate.now().isAfter(t.getApplicationDeadline())) {
            throw new IllegalArgumentException("Application deadline has passed");
        }

        if (participantRepository.existsByTournamentIdAndUserId(tournamentId, userId)) {
            throw new IllegalArgumentException("User already applied");
        }

        if (participantRepository.countByTournamentId(tournamentId) >= t.getMaxTeams()) {
            throw new IllegalArgumentException("Tournament is full");
        }

        if (participantRepository.existsByTournamentIdAndLicenseNumberIgnoreCase(tournamentId, req.licenseNumber)) {
            throw new IllegalArgumentException("License already used");
        }

        if (participantRepository.existsByTournamentIdAndRanking(tournamentId, req.ranking)) {
            throw new IllegalArgumentException("Ranking already used");
        }

        TournamentParticipant p = new TournamentParticipant();
        p.setTournament(t);
        p.setUser(u);
        p.setLicenseNumber(req.licenseNumber);
        p.setRanking(req.ranking);

        p = participantRepository.save(p);

        return new ApplyTournamentResponse(
                t.getId(),
                u.getId(),
                p.getLicenseNumber(),
                p.getRanking(),
                p.getAppliedAt()
        );
    }

    @Transactional(readOnly = true)
    public Page<TournamentResponse> myUpcomingTournaments(Long userId, Pageable pageable) {
        userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        var list = participantRepository.findByUserId(userId).stream()
                .map(TournamentParticipant::getTournament)
                .filter(t -> "UPCOMING".equalsIgnoreCase(t.getStatus()))
                .distinct()
                .map(this::toResponse)
                .toList();

        return new PageImpl<>(list, pageable, list.size());
    }

    @Transactional
    public SeedTournamentResponse seedTournament(Long tournamentId, Long currentUserId) {
        Tournament t = tournamentRepository.findByIdForUpdate(tournamentId)
                .orElseThrow(() -> new IllegalArgumentException("Tournament not found"));

        if (t.getOrganizer() == null || t.getOrganizer().getId() == null) {
            throw new IllegalArgumentException("Tournament organizer not set");
        }
        if (!t.getOrganizer().getId().equals(currentUserId)) {
            throw new IllegalArgumentException("Only organizer can seed this tournament");
        }
       
        if (matchRepository.existsByTournamentId(tournamentId)) {
            throw new IllegalArgumentException("Tournament already seeded");
        }

        List<TournamentParticipant> participants = participantRepository.findByTournamentIdOrderByRankingAsc(tournamentId);
        if (participants.isEmpty()) {
            throw new IllegalArgumentException("No participants to seed");
        }

        int n = participants.size();
        int totalSlots = nextPowerOfTwo(n);
        int totalRounds = (int) (Math.log(totalSlots) / Math.log(2));

        TournamentParticipant[] slots = new TournamentParticipant[totalSlots];
        for (int i = 0; i < n; i++) slots[i] = participants.get(i);

        List<Match> matchesToSave = new ArrayList<>();

        int round1Matches = totalSlots / 2;
        for (int i = 0; i < round1Matches; i++) {
            TournamentParticipant p1 = slots[i];
            TournamentParticipant p2 = slots[totalSlots - 1 - i];
            if (p1 == null && p2 == null) continue;
            if (p1 == null) { 
                TournamentParticipant tmp = p1;
                p1 = p2;
                p2 = tmp;
            }

            Match m = new Match();
            m.setTournament(t);
            m.setPlayer1(p1);
            m.setPlayer2(p2);
            m.setRoundNo(1);
            m.setMatchNo(i + 1);
            m.setStatus(p2 == null ? "COMPLETED" : "SCHEDULED");
            m.setConfirmedWinnerUserId(p2 == null ? p1.getUser().getId() : null);
            matchesToSave.add(m);
        }

        for (int round = 2; round <= totalRounds; round++) {
            int count = totalSlots / (1 << round);
            for (int i = 0; i < count; i++) {
                Match m = new Match();
                m.setTournament(t);
                m.setPlayer1(null);
                m.setPlayer2(null);
                m.setRoundNo(round);
                m.setMatchNo(i + 1);
                m.setStatus("SCHEDULED");
                matchesToSave.add(m);
            }
        }

        List<Match> saved = matchRepository.saveAll(matchesToSave);

        for (Match m : saved) {
            if (m.getRoundNo() == 1 && "COMPLETED".equalsIgnoreCase(m.getStatus()) && m.getConfirmedWinnerUserId() != null) {
                propagateWinnerToNextRound(tournamentId, m.getRoundNo(), m.getMatchNo(), m.getPlayer1());
            }
        }

        List<MatchResponse> responses = saved.stream().map(this::toMatchResponse).toList();
        return new SeedTournamentResponse(tournamentId, participants.size(), saved.size(), responses);
    }

    @Transactional(readOnly = true)
    public List<MatchResponse> listMatches(Long tournamentId) {
        tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new IllegalArgumentException("Tournament not found"));

        return matchRepository.findByTournamentIdOrderByRoundNoAscMatchNoAsc(tournamentId)
                .stream()
                .map(this::toMatchResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MatchResponse> myUpcomingGames(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return matchRepository.findUpcomingByUserId(userId)
                .stream()
                .map(this::toMatchResponse)
                .toList();
    }

    public MatchResponse toMatchResponsePublic(Match m) {
        return toMatchResponse(m);
    }

    private MatchResponse toMatchResponse(Match m) {
        TournamentParticipant p1 = m.getPlayer1();
        TournamentParticipant p2 = m.getPlayer2();

        Long p1UserId = null;
        String p1License = null;
        Integer p1Ranking = null;

        Long p2UserId = null;
        String p2License = null;
        Integer p2Ranking = null;

        if (p1 != null) {
            p1UserId = p1.getUser().getId();
            p1License = p1.getLicenseNumber();
            p1Ranking = p1.getRanking();
        }
        if (p2 != null) {
            p2UserId = p2.getUser().getId();
            p2License = p2.getLicenseNumber();
            p2Ranking = p2.getRanking();
        }

        return new MatchResponse(
                m.getId(),
                m.getTournament().getId(),
                m.getRoundNo(),
                m.getMatchNo(),
                p1UserId,
                p1License,
                p1Ranking == null ? 0 : p1Ranking,
                p2UserId,
                p2License,
                p2Ranking,
                m.getCreatedAt(),
                m.getStatus(),
                m.getPlayer1SelectedWinnerUserId(),
                m.getPlayer2SelectedWinnerUserId(),
                m.getConfirmedWinnerUserId()
        );
    }

    @Transactional
    public void propagateWinnerToNextRound(Long tournamentId, int roundNo, int matchNo, TournamentParticipant winner) {
        if (winner == null) return;
        int nextRound = roundNo + 1;
        int nextMatchNo = (matchNo + 1) / 2;

        var nextOpt = matchRepository.findByTournamentIdAndRoundNoAndMatchNoForUpdate(tournamentId, nextRound, nextMatchNo);
        if (nextOpt.isEmpty()) {
            return; 
        }

        Match next = nextOpt.get();
        boolean goesToPlayer1Slot = (matchNo % 2 == 1);
        if (goesToPlayer1Slot) {
            next.setPlayer1(winner);
        } else {
            next.setPlayer2(winner);
        }
        matchRepository.save(next);
    }

    private int nextPowerOfTwo(int n) {
        int p = 1;
        while (p < n) p <<= 1;
        return p;
    }

    private TournamentResponse toResponse(Tournament t) {
        String maps = "https://www.google.com/maps/search/?api=1&query=" +
                URLEncoder.encode(t.getCity(), StandardCharsets.UTF_8);

        return new TournamentResponse(
                t.getId(),
                t.getName(),
                t.getCity(),
                t.getStartDate(),
                t.getEndDate(),
                t.getApplicationDeadline(),
                t.getMaxTeams(),
                t.getSportType(),
                t.getStatus(),
                t.getSponsorLogos(),
                t.getRankedPlayers(),
                t.getDescription(),
                t.getCreatedAt(),
                maps
        );
    }

    private String calcStatus(LocalDate start, LocalDate end) {
        LocalDate today = LocalDate.now();
        if (today.isBefore(start)) return "UPCOMING";
        if (!today.isAfter(end)) return "ONGOING";
        return "FINISHED";
    }

    private String trimToNull(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }
}
