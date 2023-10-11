package com.example.booksstoreappbackend.controller;

import com.example.booksstoreappbackend.controller.dto.GrantDto;
import com.example.booksstoreappbackend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Rest controller for admin panel.
 */
@RestController
@RequestMapping("/api/v1/admins")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ADMIN')")
public class AdminController {

  private final UserService userService;

  /**
   * Grant endpoint, change role of provided user.
   *
   * @param grantDto - user dto, contains email and role to update.
   * @return - grantDto with updated role.
   */
  @PostMapping
  public ResponseEntity<GrantDto> grantUserNewRole(@RequestBody GrantDto grantDto) {
    var response = userService.grantUserNewRole(grantDto.email(), grantDto.role());
    return ResponseEntity.ok(response);
  }

}
