package com.example.booksstoreappbackend.controller.dto;

import java.util.UUID;

public record BookResponseDto(UUID id,
                              String title,
                              String author,
                              Double price,
                              String description,
                              Integer quantity,
                              SellerDTO seller,
                              String imageUrl,
                              byte[] image) {
}
