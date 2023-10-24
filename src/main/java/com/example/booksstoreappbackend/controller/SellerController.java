package com.example.booksstoreappbackend.controller;

import com.example.booksstoreappbackend.controller.dto.BookDto;
import com.example.booksstoreappbackend.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/sellers")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('SELLER')")
public class SellerController {

  private final UserService userService;

  @PostMapping(value = "/{sellerId}/books", consumes = {"multipart/form-data"})
  public ResponseEntity<?> addBook(@PathVariable UUID sellerId, @ModelAttribute @Valid BookDto book) {
    return new ResponseEntity<>(userService.saveSellerBook(sellerId, book), HttpStatus.CREATED);
  }

  @PutMapping(value = "/{sellerId}/books/{bookId}", consumes = {"multipart/form-data"})
  public ResponseEntity<?> updateBook(@PathVariable UUID sellerId, @PathVariable UUID bookId,
                                      @ModelAttribute @Valid BookDto book) {
    return new ResponseEntity<>(userService.updateSellerBook(sellerId, bookId, book), HttpStatus.OK);
  }

  @DeleteMapping(value = "/{sellerId}/books/{bookId}")
    public ResponseEntity<?> deleteBook(@PathVariable UUID sellerId, @PathVariable UUID bookId) {
        userService.deleteSellerBook(sellerId, bookId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
