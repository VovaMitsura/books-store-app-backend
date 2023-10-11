package com.example.booksstoreappbackend.controller.dto;

import lombok.Builder;

/**
 * Authentication response DTO.
 *
 * @param token - jwt token.
 */
@Builder
public record AuthenticationResponse(String token) {
}
