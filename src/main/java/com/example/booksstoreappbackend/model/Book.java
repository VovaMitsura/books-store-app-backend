package com.example.booksstoreappbackend.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "books")
public class Book {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;
  private String title;
  private String author;
  private Double price;
  private String description;
  private Integer quantity;
  private String url;
  @ManyToOne
  @JoinColumn(name = "seller_id", nullable = false)
  private User seller;
  @ManyToOne
  @JoinColumn(name = "genre_id", nullable = false)
  private Genre genre;
  @ManyToOne
  @JoinColumn(name = "bonus_id")
  private Bonus bonus;
  @ManyToOne
  @JoinColumn(name = "discount_id")
  private Discount discount;

}
