package com.example.booksstoreappbackend.repository;

import com.example.booksstoreappbackend.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BookRepository extends JpaRepository<Book, UUID> {
}
