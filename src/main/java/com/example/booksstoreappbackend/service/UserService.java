package com.example.booksstoreappbackend.service;


import com.example.booksstoreappbackend.controller.dto.GrantDto;
import com.example.booksstoreappbackend.exception.ApplicationExceptionHandler;
import com.example.booksstoreappbackend.exception.NotFoundException;
import com.example.booksstoreappbackend.model.Role;
import com.example.booksstoreappbackend.model.User;
import com.example.booksstoreappbackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service for interacting with user entities.
 */
@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;

  /**
   * get user by provided email.
   *
   * @param userEmail - email to find user in db.
   * @return - user if exists.
   */
  public User getUserByEmail(String userEmail) {
    return userRepository.findByEmail(userEmail).orElseThrow(() ->
            new NotFoundException(ApplicationExceptionHandler.NOT_FOUND,
                    String.format("User with email %s not found", userEmail)));
  }

  /**
   * Grant user provided role.
   *
   * @param userEmail - user to grant new role.
   * @param role - new role to grant.
   * @return - user with updated role.
   */
  public GrantDto grantUserNewRole(String userEmail, Role role) {
    var user = getUserByEmail(userEmail);
    user.setRole(role);
    user = userRepository.save(user);
    return GrantDto.builder()
            .email(user.getEmail())
            .role(user.getRole())
            .build();
  }

}
