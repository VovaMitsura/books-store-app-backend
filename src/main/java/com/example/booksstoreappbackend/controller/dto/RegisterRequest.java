package com.example.booksstoreappbackend.controller.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;


/**
 * Register (sing/up) DTO.
 *
 * @param firstname - user firstname.
 * @param lastname  - user lastname.
 * @param email     - user email.
 * @param password  - user password.
 */
@Builder
public record RegisterRequest(@NotBlank String firstname, @NotBlank String lastname,
                              @NotBlank @Email(message = "Email should be valid",
                                      regexp = "^[A-Za-z0-9+_.-]+@(.+)$") String email,
                              @NotBlank String password,
                              @NotBlank String role) {

}

