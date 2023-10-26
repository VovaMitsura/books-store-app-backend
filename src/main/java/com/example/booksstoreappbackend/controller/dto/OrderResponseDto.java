package com.example.booksstoreappbackend.controller.dto;

import java.util.List;
import java.util.UUID;

public record OrderResponseDto(UUID id,

                               String status,
                               Double total,
                               List<OrderDetailsResponseDto> orderDetails) {

}