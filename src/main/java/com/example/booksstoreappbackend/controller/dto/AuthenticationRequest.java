package com.example.booksstoreappbackend.controller.dto;

import lombok.Builder;

/**
 * Authentication request (sing/in) DTO.
 *
 * @param email - user email.
 * @param password - user password.
 */
@Builder
public record AuthenticationRequest(String email, String password) {
}
