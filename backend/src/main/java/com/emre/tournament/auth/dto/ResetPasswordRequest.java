package com.emre.tournament.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ResetPasswordRequest {

    @NotBlank
    public String token;

    @NotBlank
    @Size(min = 6, max = 72)
    public String newPassword;
}
