package com.example.booksstoreappbackend.controller.dto;

import com.example.booksstoreappbackend.model.CreditCard;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record PaymentRequestDto(@NotNull(message = "order id cannot be empty") UUID orderID,
                                @NotNull(message = "credit card cannot be empty") CreditCard creditCard) {
}
