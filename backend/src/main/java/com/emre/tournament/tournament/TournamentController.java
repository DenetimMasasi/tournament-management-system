package com.emre.tournament.tournament;

import com.emre.tournament.tournament.dto.*;
import com.emre.tournament.tournament.service.TournamentService;
import com.emre.tournament.user.entity.User;
import com.emre.tournament.user.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tournaments")
public class TournamentController {

    private final TournamentService tournamentService;
    private final UserRepository userRepository;

    public TournamentController(TournamentService tournamentService, UserRepository userRepository) {
        this.tournamentService = tournamentService;
        this.userRepository = userRepository;
    }

    @PostMapping
    public TournamentResponse create(
            @RequestBody @Valid CreateTournamentRequest req,
            Authentication authentication
    ) {
        Long userId = currentUserId(authentication);
        return tournamentService.create(req, userId);
    }

    @PutMapping("/{id}")
    public TournamentResponse update(
            @PathVariable Long id,
            @RequestBody @Valid UpdateTournamentRequest req,
            Authentication authentication
    ) {
        Long userId = currentUserId(authentication);
        return tournamentService.update(id, userId, req);
    }

    @PostMapping("/{id}/seed")
    public SeedTournamentResponse seed(
            @PathVariable Long id,
            Authentication authentication
    ) {
        Long userId = currentUserId(authentication);
        return tournamentService.seedTournament(id, userId);
    }

    @GetMapping("/{id}/matches")
    public List<MatchResponse> matches(@PathVariable Long id) {
        return tournamentService.listMatches(id);
    }

    @GetMapping("/my-upcoming-games")
    public List<MatchResponse> myUpcomingGames(Authentication authentication) {
        Long userId = currentUserId(authentication);
        return tournamentService.myUpcomingGames(userId);
    }

    @GetMapping
    public Page<TournamentResponse> list(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String sportType,
            @RequestParam(required = false) String status,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        return tournamentService.list(q, city, sportType, status, pageable);
    }

    @GetMapping("/upcoming")
    public Page<TournamentResponse> upcoming(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String sportType,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        return tournamentService.upcoming(q, city, sportType, pageable);
    }

    @GetMapping("/{id}")
    public TournamentResponse getById(@PathVariable Long id) {
        return tournamentService.getById(id);
    }

    @PostMapping("/{id}/apply")
    public ApplyTournamentResponse apply(
            @PathVariable Long id,
            @RequestBody @Valid ApplyTournamentRequest req,
            Authentication authentication
    ) {
        Long userId = currentUserId(authentication);
        return tournamentService.apply(id, userId, req);
    }

    @GetMapping("/my-upcoming")
    public Page<TournamentResponse> myUpcoming(
            Authentication authentication,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        Long userId = currentUserId(authentication);
        return tournamentService.myUpcomingTournaments(userId, pageable);
    }

    private Long currentUserId(Authentication authentication) {
        if (authentication == null) {
            throw new IllegalArgumentException("Authentication required");
        }
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return user.getId();
    }
}
