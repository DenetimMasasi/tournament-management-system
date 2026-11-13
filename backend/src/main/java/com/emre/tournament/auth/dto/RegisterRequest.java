package com.emre.tournament.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegisterRequest {

    @NotBlank @Size(max = 80)
    public String firstName;

    @NotBlank @Size(max = 80)
    public String lastName;

    @NotBlank @Email @Size(max = 180)
    public String email;

    @NotBlank @Size(min = 6, max = 72)
    public String password;
}
