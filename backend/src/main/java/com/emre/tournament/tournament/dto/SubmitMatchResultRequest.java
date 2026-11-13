package com.emre.tournament.tournament.dto;

import jakarta.validation.constraints.NotNull;

public class SubmitMatchResultRequest {

    @NotNull
    public Long winnerUserId;
}
