package com.example.booksstoreappbackend.service;


import java.io.IOException;
import java.net.URI;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

import com.example.booksstoreappbackend.controller.dto.AuthenticationRequest;
import com.example.booksstoreappbackend.controller.dto.AuthenticationResponse;
import com.example.booksstoreappbackend.controller.dto.RegisterRequest;
import com.example.booksstoreappbackend.exception.ApplicationExceptionHandler;
import com.example.booksstoreappbackend.exception.EntityConflictException;
import com.example.booksstoreappbackend.exception.MailingException;
import com.example.booksstoreappbackend.model.Role;
import com.example.booksstoreappbackend.model.User;
import com.example.booksstoreappbackend.model.VerificationToken;
import com.example.booksstoreappbackend.repository.UserRepository;
import com.example.booksstoreappbackend.security.JwtService;
import freemarker.template.TemplateException;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 * Authentication service implementation logic for sing in/up user.
 */
@Service
@RequiredArgsConstructor
public class AuthenticationService {

  public static final String CONFIRMATION = "confirmation";

  @Value("${link.confirmation}")
  private String confirmationLink;
  private static final String TOKEN_URL = "https://oauth2.googleapis.com/token";

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final AuthenticationManager authenticationManager;
  private final VerificationService verificationService;
  private final MailingService mailingService;

  /**
   * Registering new users to system.
   *
   * @param registerRequest - accept user credentials (name, email, password, ect.).
   * @return jwt, allows to interact with api.
   */
  public String register(RegisterRequest registerRequest) {
    var user = User.builder()
            .firstName(registerRequest.firstname())
            .lastName(registerRequest.lastname())
            .email(registerRequest.email())
            .password(passwordEncoder.encode(registerRequest.password()))
            .role(Role.valueOf(registerRequest.role()))
            .confirmed(Boolean.FALSE)
            .build();

    var verToken = new AtomicReference<VerificationToken>();
    userRepository.findByEmail(user.getEmail())
            .ifPresentOrElse(existingUser -> {

              if (existingUser.getConfirmed().equals(Boolean.TRUE)) {
                throw new EntityConflictException(ApplicationExceptionHandler.DUPLICATE_ENTRY,
                        String.format("User with email %s already exists",
                                existingUser.getEmail()));
              } else {
                verToken.set(verificationService.update(existingUser));
              }
            }, () -> verToken.set(verificationService.save(user)));

    var payloads = new HashMap<String, Object>();
    payloads.put("user", user);
    payloads.put(CONFIRMATION, confirmationLink + verToken.get().getToken());

    try {
      mailingService.send(user, CONFIRMATION, payloads);
    } catch (IOException | MessagingException | TemplateException e) {
      throw new MailingException(ApplicationExceptionHandler.MAILING_EXCEPTION, e.getMessage());
    }

    return String.format("Thanks for registering, please confirm your account %s",
            user.getEmail());
  }

  /**
   * Activate user account.
   *
   * @param token - user confiramtion token.
   * @return - jwt.
   */
  public AuthenticationResponse activate(String token) {
    var verToken = verificationService.findByToken(token);
    var user = verToken.getUser();

    if (!user.isEnabled()) {
      var currentTime = new Timestamp(System.currentTimeMillis());
      if (verToken.getExpiryDate().before(currentTime)) {
        throw new EntityConflictException(ApplicationExceptionHandler.TOKEN_EXCEPTION,
                String.format("Your verification token has expired %s", verToken.getExpiryDate()));
      }

      user.setConfirmed(true);
      userRepository.save(user);
    }

    var jwtToken = jwtService.generateToken(user);
    return AuthenticationResponse.builder().token(jwtToken).build();
  }

  /**
   * Authentication of authorized users.
   *
   * @param authenticationRequest - user email and password.
   * @return jwt, allows to interact with api.
   */
  public AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest) {
    try {
      authenticationManager.authenticate(
              new UsernamePasswordAuthenticationToken(
                      authenticationRequest.email(),
                      authenticationRequest.password()));
    } catch (DisabledException | LockedException | BadCredentialsException e) {
      throw new EntityConflictException(
              ApplicationExceptionHandler.NOT_FOUND,
              String.format("User %s not registered or email not confirmed",
                      authenticationRequest.email()));
    }

    var user = userRepository.findByEmail(authenticationRequest.email()).orElseThrow();
    var claims = new HashMap<String, Object>();
    claims.put("id", user.getId());
    claims.put("role", user.getRole());
    var jwt = jwtService.generateToken(claims, user);

    return AuthenticationResponse.builder().token(jwt).build();
  }
}