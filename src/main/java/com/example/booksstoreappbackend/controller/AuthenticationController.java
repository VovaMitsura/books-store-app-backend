package com.example.booksstoreappbackend.controller;


import com.example.booksstoreappbackend.controller.dto.AuthenticationRequest;
import com.example.booksstoreappbackend.controller.dto.AuthenticationResponse;
import com.example.booksstoreappbackend.controller.dto.RegisterRequest;
import com.example.booksstoreappbackend.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Rest controller for sing in/up.
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

  private final AuthenticationService authenticationService;

  /**
   * Register endpoint, allows users sing up to the system.
   *
   * @param registerRequest - accept credentials for registration.
   * @return jwt, which allows to interact with app.
   */
  @PostMapping("/register")
  public ResponseEntity<Map<String, Object>> register(
          @RequestBody @Valid RegisterRequest registerRequest) {
    var result = authenticationService.register(registerRequest);
    var response = new HashMap<String, Object>();
    response.put("message", result);
    return ResponseEntity.ok(response);
  }

  /**
   * Activation Endpoint, enables users account.
   *
   * @param token - user confirmation token.
   * @return - jwt.
   */
  @GetMapping("/confirmation")
  public ResponseEntity<AuthenticationResponse> activation(
          @RequestParam(name = "token") @Valid String token) {
    return ResponseEntity.ok(authenticationService.activate(token));
  }

  /**
   * Authenticate endpoint, allows users sing in to the system.
   *
   * @param authenticationRequest - accepts user email and password to sing in.
   * @return jwt, which allows to interact with app.
   */
  @PostMapping("/authenticate")
  public ResponseEntity<AuthenticationResponse> authenticate(
          @RequestBody @Valid AuthenticationRequest authenticationRequest) {
    return ResponseEntity.ok(authenticationService.authenticate(authenticationRequest));
  }
}
