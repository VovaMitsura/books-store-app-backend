package com.example.booksstoreappbackend.controller.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

/**
 * Authentication request (sing/in) DTO.
 *
 * @param email    - user email.
 * @param password - user password.
 */
@Builder
public record AuthenticationRequest(@NotBlank(message = "email cannot be blank") String email,
                                    @NotBlank(message = "password cannot be blank") String password) {
}
