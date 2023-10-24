package com.example.booksstoreappbackend.controller.dto;

import java.util.UUID;

public record SellerDTO(UUID id, String firstName, String lastName, String email) {
}
