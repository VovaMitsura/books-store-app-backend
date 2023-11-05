package com.example.booksstoreappbackend.service;

import com.example.booksstoreappbackend.controller.dto.BookDto;
import com.example.booksstoreappbackend.controller.dto.CommentRequest;
import com.example.booksstoreappbackend.controller.dto.mapper.BookMapper;
import com.example.booksstoreappbackend.controller.dto.BookResponseDto;
import com.example.booksstoreappbackend.exception.ApplicationExceptionHandler;
import com.example.booksstoreappbackend.exception.NotFoundException;
import com.example.booksstoreappbackend.model.Book;
import com.example.booksstoreappbackend.model.Comment;
import com.example.booksstoreappbackend.repository.BookRepository;
import com.example.booksstoreappbackend.repository.CommentRepository;
import com.example.booksstoreappbackend.s3.S3Service;
import com.example.booksstoreappbackend.security.util.UserPrincipalUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Service for interacting with books.
 */
@Service
@RequiredArgsConstructor
public class BookService {

  private final BookRepository bookRepository;
  private final UserService userService;
  private final S3Service s3Service;
  private final BookMapper bookMapper;
  private final CommentRepository commentRepository;

  public BookResponseDto getBookById(UUID bookId) {
    var book = bookRepository.findById(bookId).orElseThrow(() ->
            new NotFoundException(ApplicationExceptionHandler.NOT_FOUND,
                    String.format("Book with id %s not found", bookId)));

    var image = s3Service.getObject(book.getUrl());

    return bookMapper.toResponseDto(book, image);
  }

  public Book findByIdWithoutImage(UUID bookId) {
    return bookRepository.findById(bookId).orElseThrow(() ->
            new NotFoundException(ApplicationExceptionHandler.NOT_FOUND,
                    String.format("Book with id %s not found", bookId)));
  }

  public List<BookResponseDto> getAllBooks() {
    var books = bookRepository.findAll();
    return books.stream().map(book -> {
      var image = s3Service.getObject(book.getUrl());
      return bookMapper.toResponseDto(book, image);
    }).toList();
  }

  public BookResponseDto setPreferenceBook(UUID bookId, String email, boolean isPreferred) {
    var user = userService.getUserByEmail(email);
    var book = findByIdWithoutImage(bookId);

    if (isPreferred) {
      user.getLikedBooks().add(book);
      book.getLikedByUsers().add(user);
    } else {
      user.getLikedBooks().remove(book);
      book.getLikedByUsers().remove(user);
    }

    book = bookRepository.save(book);

    return bookMapper.toResponseDto(book, null);
  }

  public BookResponseDto addCommentToBook(CommentRequest request, String email) {
    var user = userService.getUserByEmail(email);
    var book = findByIdWithoutImage(request.bookId());

    var comment = Comment.builder()
            .message(request.comment())
            .date(LocalDateTime.now())
            .commenter(user)
            .commentedBook(book)
            .build();

    comment = commentRepository.save(comment);

    user.getComments().add(comment);
    book.getComments().add(comment);

    book = bookRepository.save(book);

    return bookMapper.toResponseDto(book, null);
  }

  public void deleteCommentFromBook(UUID commentId, String email) {
    var user = UserPrincipalUtil.extractUserPrinciple();
    var comment = commentRepository.findById(commentId).orElseThrow(() ->
            new NotFoundException(ApplicationExceptionHandler.NOT_FOUND,
                    String.format("Comment with id %s not found", commentId)));

    if (!Objects.equals(comment.getCommenter().getEmail(), user.getEmail())) {
      throw new NotFoundException(ApplicationExceptionHandler.NOT_FOUND,
              String.format("Comment with id %s not found", commentId));
    }

    commentRepository.delete(comment);
  }
}
