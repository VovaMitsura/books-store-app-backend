package com.example.booksstoreappbackend.repository;

import java.util.Optional;
import java.util.UUID;

import com.example.booksstoreappbackend.model.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * JPA repository for interaction with tokens from database.
 */
@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, UUID> {

  Optional<VerificationToken> findByUserId(UUID userId);

  Optional<VerificationToken> findByToken(String token);
}