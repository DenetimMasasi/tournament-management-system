package com.emre.tournament.tournament;

import com.emre.tournament.tournament.dto.MatchResponse;
import com.emre.tournament.tournament.dto.SubmitMatchResultRequest;
import com.emre.tournament.tournament.service.MatchService;
import com.emre.tournament.user.entity.User;
import com.emre.tournament.user.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/matches")
public class MatchController {

    private final MatchService matchService;
    private final UserRepository userRepository;

    public MatchController(MatchService matchService, UserRepository userRepository) {
        this.matchService = matchService;
        this.userRepository = userRepository;
    }

    @PostMapping("/{id}/result")
    public MatchResponse submitResult(
            @PathVariable Long id,
            @RequestBody @Valid SubmitMatchResultRequest req,
            Authentication authentication
    ) {
        Long userId = currentUserId(authentication);
        return matchService.submitResult(id, userId, req);
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
