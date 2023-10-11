package com.example.booksstoreappbackend.controller;


import com.example.booksstoreappbackend.model.User;
import com.example.booksstoreappbackend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Rest controller for user info.
 */
@RestController
@RequestMapping("/api/v1/me")
@PreAuthorize("hasAnyAuthority('SELLER', 'CUSTOMER', 'ADMIN')")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  /**
   * User info endpoint, allows authorized users to see their info.
   *
   * @param authentication - authentication object, which contains user credentials.
   * @return - user object.
   */
  @GetMapping
  public ResponseEntity<User> getCurrentUser(Authentication authentication) {
    var response = userService.getUserByEmail(authentication.getName());
    return ResponseEntity.ok(response);
  }
}
