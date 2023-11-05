package com.example.booksstoreappbackend.controller;

import com.example.booksstoreappbackend.controller.dto.CommentRequest;
import com.example.booksstoreappbackend.security.util.UserPrincipalUtil;
import com.example.booksstoreappbackend.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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

  @GetMapping(value = "/{bookId}/like")
  public ResponseEntity<?> likeBook(@PathVariable UUID bookId) {
    var user = UserPrincipalUtil.extractUserPrinciple();
    return new ResponseEntity<>(bookService.setPreferenceBook(bookId, user.getEmail(), true), HttpStatus.OK);
  }

  @DeleteMapping(value = "/{bookId}/unlike")
  public ResponseEntity<?> unlikeBook(@PathVariable UUID bookId) {
    var user = UserPrincipalUtil.extractUserPrinciple();
    return new ResponseEntity<>(bookService.setPreferenceBook(bookId, user.getEmail(), false), HttpStatus.ACCEPTED);
  }

  @PostMapping(value = "/comments")
  public ResponseEntity<?> addCommentToBook(@RequestBody CommentRequest request) {
    var user = UserPrincipalUtil.extractUserPrinciple();
    return new ResponseEntity<>(bookService.addCommentToBook(request, user.getEmail()), HttpStatus.CREATED);
  }

  @DeleteMapping(value = "/comments/{commentId}")
  public ResponseEntity<?> deleteCommentFromBook(@PathVariable UUID commentId) {
    var user = UserPrincipalUtil.extractUserPrinciple();
    bookService.deleteCommentFromBook(commentId, user.getEmail());
    return ResponseEntity.accepted().build();
  }
}
