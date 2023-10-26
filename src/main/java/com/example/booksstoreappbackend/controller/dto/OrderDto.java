package com.example.booksstoreappbackend.controller.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record OrderDto(@NotNull(message = "book id cannot be empty") UUID bookId,
                       @Min(1) Integer quantity) {
}
