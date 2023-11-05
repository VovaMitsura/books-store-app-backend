package com.example.booksstoreappbackend.controller.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record CommentResponseDto(UUID id,
                                 String message,
                                 LocalDateTime date,
                                 String commenterEmail) {
}
