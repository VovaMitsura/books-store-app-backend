package com.example.booksstoreappbackend.repository;

import com.example.booksstoreappbackend.model.Bonus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BonusRepository extends JpaRepository<Bonus, UUID> {
  Optional<Bonus> findByName(String bonusName);
}
