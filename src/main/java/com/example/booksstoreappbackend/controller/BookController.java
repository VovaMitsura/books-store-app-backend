package com.example.booksstoreappbackend.controller;

import com.example.booksstoreappbackend.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/books")
@RequiredArgsConstructor
@PreAuthorize("hasAnyAuthority('CUSTOMER', 'ADMIN', 'SELLER')")
public class BookController {

  private final BookService bookService;

  @GetMapping(value = "/{bookId}")
  public ResponseEntity<?> getBookById(@PathVariable UUID bookId) {
    return new ResponseEntity<>(bookService.getBookById(bookId), HttpStatus.OK);
  }

  @GetMapping()
  public ResponseEntity<?> getAllBooks() {
    return new ResponseEntity<>(bookService.getAllBooks(), HttpStatus.OK);
  }

}
