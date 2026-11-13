package com.emre.tournament.auth.dto;

public class LoginResponse {

    public Long id;
    public String email;
    public String firstName;
    public String lastName;

    public LoginResponse(Long id, String email, String firstName, String lastName) {
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
    }
}
