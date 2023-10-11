package com.example.booksstoreappbackend.repository;

import java.util.Optional;
import java.util.UUID;

import com.example.booksstoreappbackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * User JPA repository interface, allows retrieve user from database.
 */
@Repository
public interface  UserRepository extends JpaRepository<User, UUID> {

  Optional<User> findByEmail(String email);

}