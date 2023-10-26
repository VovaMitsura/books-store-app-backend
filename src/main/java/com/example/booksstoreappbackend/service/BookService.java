package com.example.booksstoreappbackend.service;

import com.example.booksstoreappbackend.controller.dto.BookDto;
import com.example.booksstoreappbackend.controller.dto.mapper.BookMapper;
import com.example.booksstoreappbackend.controller.dto.BookResponseDto;
import com.example.booksstoreappbackend.exception.ApplicationExceptionHandler;
import com.example.booksstoreappbackend.exception.NotFoundException;
import com.example.booksstoreappbackend.model.Book;
import com.example.booksstoreappbackend.repository.BookRepository;
import com.example.booksstoreappbackend.s3.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
  private final S3Service s3Service;
  private final BookMapper bookMapper;

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

}
