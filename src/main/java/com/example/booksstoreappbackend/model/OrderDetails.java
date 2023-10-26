package com.example.booksstoreappbackend.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "order_details")
public class OrderDetails {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @ManyToOne
  @JoinColumn(name = "book_id")
  private Book book;

  @ManyToOne
  @JoinColumn(name = "order_id")
  @JsonBackReference
  private Order order;

  private int quantity;

  @Transient
  public double getTotalPrice() {
    if (book != null) {
      return book.getDiscount() == null ? book.getPrice() * quantity :
              book.getPrice() * quantity - book.getPrice() * quantity * book.getDiscount().getPercentage();
    }
    return 0;
  }
}
