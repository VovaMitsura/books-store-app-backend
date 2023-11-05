package com.example.booksstoreappbackend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.proxy.HibernateProxy;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
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
  @Builder.Default
  @ManyToMany(mappedBy = "likedBooks")
  private Set<User> likedByUsers = new HashSet<>();
  @OneToMany(mappedBy = "commentedBook", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private Set<Comment> comments = new HashSet<>();

  @Override
  public final boolean equals(Object o) {
    if (this == o) return true;
    if (o == null) return false;
    Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
    Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
    if (thisEffectiveClass != oEffectiveClass) return false;
    Book book = (Book) o;
    return getId() != null && Objects.equals(getId(), book.getId());
  }

  @Override
  public final int hashCode() {
    return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
  }
}
