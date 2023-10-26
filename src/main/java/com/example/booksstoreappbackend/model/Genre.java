package com.example.booksstoreappbackend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "genres")
public class Genre {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;
  private String name;
  private String description;
  @OneToMany(mappedBy = "genre", cascade = CascadeType.ALL)
  private List<Book> books;
}
