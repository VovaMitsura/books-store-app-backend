package com.example.booksstoreappbackend.repository;

import com.example.booksstoreappbackend.model.Discount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface DiscountRepository extends JpaRepository<Discount, UUID> {
  Optional<Discount> findByName(String name);
}
