package com.example.booksstoreappbackend.service;

import com.example.booksstoreappbackend.controller.dto.AuthenticationRequest;
import com.example.booksstoreappbackend.controller.dto.RegisterRequest;
import com.example.booksstoreappbackend.exception.ApplicationExceptionHandler;
import com.example.booksstoreappbackend.exception.EntityConflictException;
import com.example.booksstoreappbackend.exception.NotFoundException;
import com.example.booksstoreappbackend.model.User;
import com.example.booksstoreappbackend.model.VerificationToken;
import com.example.booksstoreappbackend.repository.UserRepository;
import com.example.booksstoreappbackend.repository.VerificationTokenRepository;
import com.example.booksstoreappbackend.util.UserFactory;
import com.example.booksstoreappbackend.util.VerTokenFactory;
import freemarker.template.TemplateException;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class AuthenticationServiceTest {

  @Autowired
  AuthenticationService authenticationService;

  @MockBean
  UserRepository userRepository;
  @MockBean
  MailingService mailingService;
  @MockBean
  VerificationTokenRepository verificationTokenRepository;
  @MockBean
  AuthenticationManager authenticationManager;
  User user;

  @BeforeEach()
  void setUp() {
    var factory = new UserFactory();
    user = factory.createEntity();
  }

  @Test
  void should_register_one_user() throws MessagingException, TemplateException, IOException {
    var request = RegisterRequest.builder()
            .email(user.getEmail())
            .password(user.getPassword())
            .lastname(user.getLastName())
            .firstname(user.getFirstName())
            .role(user.getRole().toString())
            .build();

    var verToken = new VerTokenFactory()
            .createEntity();
    verToken.setUser(user);

    Mockito.when(userRepository.findByEmail(user.getEmail()))
            .thenReturn(Optional.empty());
    Mockito.when(verificationTokenRepository.save(Mockito.any(VerificationToken.class)))
            .thenReturn(verToken);
    Mockito.doNothing().when(mailingService)
            .send(Mockito.any(User.class), Mockito.anyString(), Mockito.anyString(), Mockito.anyMap());

    var response = authenticationService.register(request);

    Assertions.assertNotNull(response);
    Assertions.assertEquals(String.format("Thanks for registering, please confirm your account %s", user.getEmail()),
            response);
  }

  @Test
  void should_throw_exception_when_user_confirmed() {
    var request = RegisterRequest.builder()
            .email(user.getEmail())
            .password(user.getPassword())
            .lastname(user.getLastName())
            .firstname(user.getFirstName())
            .role(user.getRole().toString())
            .build();

    var verToken = new VerTokenFactory()
            .createEntity();
    verToken.setUser(user);
    user.setConfirmed(true);

    Mockito.when(userRepository.findByEmail(user.getEmail()))
            .thenReturn(Optional.of(user));

    var response = Assertions.assertThrows(EntityConflictException.class, () -> authenticationService.register(request));

    Assertions.assertEquals(ApplicationExceptionHandler.DUPLICATE_ENTRY, response.getErrorCode());
    Assertions.assertEquals(String.format("User with email %s already exists",
            user.getEmail()), response.getMessage());

  }

  @Test
  void should_update_ver_token_for_disabled_user() throws MessagingException, TemplateException, IOException {
    var request = RegisterRequest.builder()
            .email(user.getEmail())
            .password(user.getPassword())
            .lastname(user.getLastName())
            .firstname(user.getFirstName())
            .role(user.getRole().toString())
            .build();

    user.setConfirmed(false);
    var verToken = new VerTokenFactory()
            .createEntity();
    verToken.setUser(user);

    var updateToken = VerificationToken.builder()
            .token(verToken.getToken())
            .expiryDate(new Timestamp(System.currentTimeMillis() + 1000 * 60))
            .id(verToken.getId())
            .user(verToken.getUser())
            .build();

    Mockito.when(verificationTokenRepository.findByUserId(Mockito.any(UUID.class)))
            .thenReturn(Optional.of(verToken));
    Mockito.when(userRepository.findByEmail(user.getEmail()))
            .thenReturn(Optional.of(user));
    Mockito.when(verificationTokenRepository.save(Mockito.any(VerificationToken.class)))
            .thenReturn(updateToken);
    Mockito.doNothing().when(mailingService)
            .send(Mockito.any(User.class), Mockito.anyString(), Mockito.anyString(), Mockito.anyMap());

    var response = authenticationService.register(request);

    Assertions.assertNotNull(response);
    Assertions.assertEquals(String.format("Thanks for registering, please confirm your account %s", user.getEmail()),
            response);
  }

  @Test
  void should_change_confirmed_field_true() {
    var request = UUID.randomUUID().toString();
    user.setConfirmed(false);

    var verToken = new VerTokenFactory().createEntity();
    verToken.setToken(request);
    verToken.setExpiryDate(new Timestamp(System.currentTimeMillis() + 1000 * 60));
    verToken.setUser(user);

    var updateUser = new UserFactory().createEntity();
    updateUser.setConfirmed(true);


    Mockito.when(verificationTokenRepository.findByToken(request))
            .thenReturn(Optional.of(verToken));
    Mockito.when(userRepository.save(Mockito.any(User.class)))
            .thenReturn(updateUser);

    var response = authenticationService.activate(request);

    Assertions.assertNotNull(response);
    Assertions.assertNotNull(response.token());
  }

  @Test
  void should_throw_exception_invalid_provided_token() {

    Mockito.when(verificationTokenRepository.findByToken(Mockito.anyString()))
            .thenReturn(Optional.empty());

    var response = Assertions.assertThrows(NotFoundException.class,
            () -> authenticationService.activate("123"));

    Assertions.assertEquals(ApplicationExceptionHandler.NOT_FOUND, response.getErrorCode());
    Assertions.assertEquals(String.format("token %s not found", "123"), response.getMessage());
  }

  @Test
  void should_throw_exception_when_token_expired() {
    var request = UUID.randomUUID().toString();
    user.setConfirmed(false);
    var verToken = new VerTokenFactory().createEntity();
    verToken.setExpiryDate(new Timestamp(System.currentTimeMillis() - 1000 * 60));
    verToken.setToken(request);
    verToken.setUser(user);

    Mockito.when(verificationTokenRepository.findByToken(request))
            .thenReturn(Optional.of(verToken));

    var response = Assertions.assertThrows(EntityConflictException.class,
            () -> authenticationService.activate(request));

    Assertions.assertEquals(ApplicationExceptionHandler.TOKEN_EXCEPTION, response.getErrorCode());
    Assertions.assertEquals(String.format("Your verification token has expired %s", verToken.getExpiryDate()),
            response.getMessage());
  }

  @Test
  void should_authenticate_enabled_user() {
    var request = new AuthenticationRequest(user.getEmail(), user.getPassword());

    Mockito.when(authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    request.email(),
                    request.password())))
            .thenReturn(null);
    Mockito.when(userRepository.findByEmail(user.getEmail()))
            .thenReturn(Optional.of(user));

    var response = authenticationService.authenticate(request);

    Assertions.assertNotNull(response.token());
  }

  @Test
  void should_throw_exception_when_email_not_confirmed() {
    var request = new AuthenticationRequest(user.getEmail(), user.getPassword());

    Mockito.when(authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    request.email(),
                    request.password())))
            .thenThrow(new EntityConflictException(ApplicationExceptionHandler.NOT_FOUND,
                    String.format("User %s not registered or email not confirmed", request.email())));

    var response = Assertions.assertThrows(EntityConflictException.class,
            () -> authenticationService.authenticate(request));

    Assertions.assertEquals(ApplicationExceptionHandler.NOT_FOUND, response.getErrorCode());
    Assertions.assertEquals(String.format("User %s not registered or email not confirmed", request.email()),
            response.getMessage());

  }

}
