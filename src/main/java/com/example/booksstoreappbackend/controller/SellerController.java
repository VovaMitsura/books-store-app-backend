package com.example.booksstoreappbackend.controller;

import com.example.booksstoreappbackend.controller.dto.BookDto;
import com.example.booksstoreappbackend.controller.dto.DiscountDto;
import com.example.booksstoreappbackend.service.BookService;
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
  private final BookService bookService;

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

  @PreAuthorize("hasAnyAuthority('ADMIN', 'SELLER')")
  @PostMapping(value = "/{sellerId}/books/{bookId}/discounts")
  public ResponseEntity<?> addDiscount(@PathVariable UUID sellerId, @PathVariable UUID bookId,
                                       @RequestBody DiscountDto discountRequest) {
    return new ResponseEntity<>(bookService.addDiscount(sellerId, bookId, discountRequest), HttpStatus.CREATED);
  }

  @PreAuthorize("hasAnyAuthority('ADMIN', 'SELLER')")
  @DeleteMapping(value = "/{sellerId}/books/{bookId}/discounts")
  public ResponseEntity<?> deleteDiscount(@PathVariable UUID sellerId, @PathVariable UUID bookId) {
    bookService.deleteDiscount(sellerId, bookId);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }
}
