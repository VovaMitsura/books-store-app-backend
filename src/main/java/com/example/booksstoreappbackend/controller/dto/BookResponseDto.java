package com.example.booksstoreappbackend.controller.dto;

import java.util.List;
import java.util.UUID;

public record BookResponseDto(UUID id,
                              String title,
                              String author,
                              Double price,
                              String genre,
                              String description,
                              Integer quantity,
                              DiscountDto discount,
                              BonusResponseDto bonus,
                              SellerDTO seller,
                              List<CommentResponseDto> comments,
                              String imageUrl,
                              Boolean isLiked,
                              byte[] image) {
}
