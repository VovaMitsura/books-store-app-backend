package com.example.booksstoreappbackend.service;

import com.example.booksstoreappbackend.exception.ApplicationExceptionHandler;
import com.example.booksstoreappbackend.exception.NotFoundException;
import com.example.booksstoreappbackend.model.Genre;
import com.example.booksstoreappbackend.repository.GenreRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class GenreService {

  private final GenreRepository genreRepository;

  public Genre findByName(String name) {
    return genreRepository.findByName(name)
            .orElseThrow(() -> new NotFoundException(ApplicationExceptionHandler.NOT_FOUND,
                    "Genre with name " + name + " not found"));
  }
}
