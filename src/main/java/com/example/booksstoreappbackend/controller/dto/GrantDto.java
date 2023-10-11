package com.example.booksstoreappbackend.controller.dto;

import com.example.booksstoreappbackend.model.Role;
import lombok.Builder;

/**
 * Dto for interacting with users and their roles.
 *
 * @param email - user email.
 * @param role - user role.
 */
@Builder
public record GrantDto(String email, Role role) {
}
