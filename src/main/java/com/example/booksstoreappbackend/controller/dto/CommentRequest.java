package com.example.booksstoreappbackend.controller.dto;

import java.util.UUID;

public record CommentRequest(UUID bookId,
                             String comment) {
}
