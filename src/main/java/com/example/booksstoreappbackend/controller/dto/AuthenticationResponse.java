package com.example.booksstoreappbackend.controller.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

/**
 * Authentication response DTO.
 *
 * @param token - jwt token.
 */
@Builder
public record AuthenticationResponse(@NotBlank(message = "token cannot be blank") String token) {
}
