package com.example.booksstoreappbackend.controller.dto;

import java.util.UUID;

public record OrderDetailsResponseDto(UUID id,
                                      String bookTitle,
                                      String bookAuthor,
                                      Double bookPrice,
                                      Integer bookQuantity) {
}

