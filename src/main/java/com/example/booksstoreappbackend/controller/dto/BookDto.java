package com.example.booksstoreappbackend.controller.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

public record BookDto(@NotBlank(message = "title cannot be blank") String title,
                      @NotBlank(message = "author cannot be blank") String author,
                      @NotNull(message = "price cannot be null") @Min(value = 1, message = "price must be greater then 0") Double price,
                      @NotBlank(message = "genre cannot be null") String genre,
                      @NotBlank(message = "description cannot be blank") String description,
                      @NotNull(message = "price cannot be null") @Min(value = 1, message = "quantity cannot be less than 1") Integer quantity,
                      MultipartFile image) {

}
