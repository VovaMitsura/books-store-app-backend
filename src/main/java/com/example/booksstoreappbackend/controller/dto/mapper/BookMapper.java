package com.example.booksstoreappbackend.controller.dto.mapper;

import com.example.booksstoreappbackend.controller.dto.BookDto;
import com.example.booksstoreappbackend.controller.dto.BookResponseDto;
import com.example.booksstoreappbackend.controller.dto.SellerDTO;
import com.example.booksstoreappbackend.model.Book;
import com.example.booksstoreappbackend.model.User;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class BookMapper {

  public Book toModel(BookDto request, User seller) {
    var book = new Book();
    book.setTitle(request.title());
    book.setAuthor(request.author());
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
            book.getDescription(),
            book.getQuantity(),
            new SellerDTO(seller.getId(), seller.getFirstName(), seller.getLastName(), seller.getEmail()),
            book.getUrl(),
            image);
  }
}
