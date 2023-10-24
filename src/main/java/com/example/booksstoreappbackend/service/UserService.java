package com.example.booksstoreappbackend.service;


import com.example.booksstoreappbackend.controller.dto.BookDto;
import com.example.booksstoreappbackend.controller.dto.mapper.BookMapper;
import com.example.booksstoreappbackend.controller.dto.BookResponseDto;
import com.example.booksstoreappbackend.controller.dto.GrantDto;
import com.example.booksstoreappbackend.exception.ApplicationExceptionHandler;
import com.example.booksstoreappbackend.exception.NotFoundException;
import com.example.booksstoreappbackend.model.Book;
import com.example.booksstoreappbackend.model.Role;
import com.example.booksstoreappbackend.model.User;
import com.example.booksstoreappbackend.repository.BookRepository;
import com.example.booksstoreappbackend.repository.UserRepository;
import com.example.booksstoreappbackend.s3.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.UUID;

/**
 * Service for interacting with user entities.
 */
@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final BookRepository bookRepository;
  private final S3Service s3Service;
  private final BookMapper bookMapper;

  /**
   * get user by provided email.
   *
   * @param userEmail - email to find user in db.
   * @return - user if exists.
   */
  public User getUserByEmail(String userEmail) {
    return userRepository.findByEmail(userEmail).orElseThrow(() ->
            new NotFoundException(ApplicationExceptionHandler.NOT_FOUND,
                    String.format("User with email %s not found", userEmail)));
  }

  private User getUserById(UUID userId) {
    return userRepository.findById(userId).orElseThrow(() ->
            new NotFoundException(ApplicationExceptionHandler.NOT_FOUND,
                    String.format("User with id %s not found", userId)));
  }

  /**
   * Grant user provided role.
   *
   * @param userEmail - user to grant new role.
   * @param role      - new role to grant.
   * @return - user with updated role.
   */
  public GrantDto grantUserNewRole(String userEmail, Role role) {
    var user = getUserByEmail(userEmail);
    user.setRole(role);
    user = userRepository.save(user);
    return GrantDto.builder()
            .email(user.getEmail())
            .role(user.getRole())
            .build();
  }

  public BookResponseDto saveSellerBook(UUID sellerId, BookDto bookDto) {
    var seller = getUserById(sellerId);
    var book = bookMapper.toModel(bookDto, seller);

    saveImageToS3(bookDto, seller, book);

    book.setUrl(String.format("%s/%s", seller.getEmail(), book.getTitle()));
    book = bookRepository.save(book);

    return bookMapper.toResponseDto(book, null);
  }

  public BookResponseDto updateSellerBook(UUID sellerId, UUID bookId, BookDto bookDto) {
    var seller = getUserById(sellerId);
    var book = bookRepository.findById(bookId).orElseThrow(() ->
            new NotFoundException(ApplicationExceptionHandler.NOT_FOUND,
                    String.format("Book with id %s not found", bookId)));

    if (!book.getSeller().equals(seller)) {
      throw new NotFoundException(ApplicationExceptionHandler.NOT_FOUND,
              String.format("Book with id %s not found", bookId));
    }

    book.setTitle(bookDto.title());
    book.setAuthor(bookDto.author());
    book.setPrice(bookDto.price());
    book.setDescription(bookDto.description());
    book.setQuantity(bookDto.quantity());

    saveImageToS3(bookDto, seller, book);

    book = bookRepository.save(book);

    return bookMapper.toResponseDto(book, null);
  }

  public void deleteSellerBook(UUID sellerId, UUID bookId) {
    var book = bookRepository.findById(bookId).orElseThrow(() ->
            new NotFoundException(ApplicationExceptionHandler.NOT_FOUND,
                    String.format("Book with id %s not found", bookId)));

    if (!book.getSeller().getId().equals(sellerId)) {
      throw new NotFoundException(ApplicationExceptionHandler.NOT_FOUND,
              String.format("Book with id %s not found", bookId));
    }

    s3Service.deleteObject(book.getUrl());
    bookRepository.delete(book);
  }



  private void saveImageToS3(BookDto bookDto, User seller, Book book) {
    if (bookDto.image() != null) {
      try {
        s3Service.putObject(String.format("%s/%s", seller.getEmail(), book.getTitle()), bookDto.image().getBytes());
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
      book.setUrl(String.format("%s/%s", seller.getEmail(), book.getTitle()));
    }
  }

}
