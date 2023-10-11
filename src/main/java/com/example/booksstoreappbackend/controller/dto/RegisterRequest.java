package com.example.booksstoreappbackend.controller.dto;

import lombok.Builder;


/**
 * Register (sing/up) DTO.
 *
 * @param firstname - user firstname.
 * @param lastname - user lastname.
 * @param email - user email.
 * @param password - user password.
 */
@Builder
public record RegisterRequest(String firstname, String lastname, String email, String password, String role) {

}

