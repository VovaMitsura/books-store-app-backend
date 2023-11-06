package com.example.booksstoreappbackend.controller.dto.mapper;

import com.example.booksstoreappbackend.controller.dto.*;
import com.example.booksstoreappbackend.model.Book;
import com.example.booksstoreappbackend.model.User;
import com.example.booksstoreappbackend.service.GenreService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class BookMapper {

  private final GenreService genreService;

  public Book toModel(BookDto request, User seller) {
    var book = new Book();
    book.setTitle(request.title());
    book.setAuthor(request.author());
    book.setGenre(genreService.findByName(request.genre()));
    book.setPrice(request.price());
    book.setDescription(request.description());
    book.setQuantity(request.quantity());
    book.setSeller(seller);
    return book;
  }

  public BookResponseDto toResponseDto(Book book, byte[] image) {
    var seller = book.getSeller();

    return new BookResponseDto(book.getId(),
            book.getTitle(),
            book.getAuthor(),
            book.getPrice(),
            book.getGenre().getName(),
            book.getDescription(),
            book.getQuantity(),
            book.getDiscount() != null ? new DiscountDto(book.getDiscount().getName(), book.getDiscount().getPercentage()) : null,
            book.getBonus() != null ? new BonusResponseDto(book.getBonus().getName(), book.getBonus().getAmount()) : null,
            new SellerDTO(seller.getId(), seller.getFirstName(), seller.getLastName(), seller.getEmail()),
            book.getComments().stream().map(comment -> new CommentResponseDto(
                    comment.getId(),
                    comment.getMessage(),
                    comment.getDate(),
                    comment.getCommenter().getEmail())).toList(),
            book.getUrl(),
            book.getLikedByUsers().contains(seller),
            image);
  }
}
