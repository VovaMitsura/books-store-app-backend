package com.example.booksstoreappbackend.service;

import com.example.booksstoreappbackend.exception.ApplicationExceptionHandler;
import com.example.booksstoreappbackend.exception.EntityConflictException;
import com.example.booksstoreappbackend.exception.NotFoundException;
import com.example.booksstoreappbackend.model.User;
import com.example.booksstoreappbackend.model.VerificationToken;
import com.example.booksstoreappbackend.repository.VerificationTokenRepository;
import com.example.booksstoreappbackend.util.UserFactory;
import com.example.booksstoreappbackend.util.VerTokenFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.sql.Timestamp;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class VerificationServiceTest {

  @Autowired
  VerificationService verificationService;

  @MockBean
  VerificationTokenRepository verificationTokenRepository;

  User user = null;

  @BeforeEach
  void setUp() {
    user = new UserFactory().createEntity();
  }

  @Test
  void should_save_token_when_user_passed() {
    user.setConfirmed(false);
    var verToken = new VerTokenFactory().createEntity();
    verToken.setUser(user);

    Mockito.when(verificationTokenRepository.save(Mockito.any(VerificationToken.class)))
            .thenReturn(verToken);

    var response = verificationService.save(user);

    Assertions.assertEquals(user, response.getUser());
    Assertions.assertEquals(verToken.getToken(), response.getToken());
  }

  @Test
  void should_update_existing_token() {
    user.setConfirmed(false);
    var verToken = new VerTokenFactory().createEntity();
    verToken.setUser(user);

    var updateToken = VerificationToken
            .builder()
            .token(UUID.randomUUID().toString())
            .id(verToken.getId())
            .expiryDate(new Timestamp(System.currentTimeMillis() + 1000 * 60))
            .user(user)
            .build();

    Mockito.when(verificationTokenRepository.findByUserId(user.getId()))
            .thenReturn(Optional.of(verToken));
    Mockito.when(verificationTokenRepository.save(Mockito.any(VerificationToken.class)))
            .thenReturn(updateToken);

    var response = verificationService.update(user);

    Assertions.assertEquals(updateToken.getId(), response.getId());
    Assertions.assertEquals(updateToken.getToken(), response.getToken());
    Assertions.assertEquals(updateToken.getUser(), response.getUser());
    Assertions.assertEquals(updateToken.getExpiryDate(), response.getExpiryDate());
  }

  @Test
  void should_throw_exception_when_token_not_exists() {
    Mockito.when(verificationTokenRepository.findByUserId(user.getId()))
            .thenReturn(Optional.empty());

    var response = Assertions.assertThrows(EntityConflictException.class,
            () -> verificationService.update(user));

    Assertions.assertEquals(ApplicationExceptionHandler.NOT_FOUND, response.getErrorCode());
    Assertions.assertEquals(String.format("Token for user %s not found", user.getEmail()),
            response.getMessage());
  }

  @Test
  void should_return_existing_token() {
    var request = UUID.randomUUID().toString();
    var verToken = new VerTokenFactory().createEntity();

    Mockito.when(verificationTokenRepository.findByToken(request))
            .thenReturn(Optional.of(verToken));

    var response = verificationService.findByToken(request);

    Assertions.assertEquals(verToken, response);
  }

  @Test
  void should_throw_exception_when_confirmation_token_not_exists() {
    var request = UUID.randomUUID().toString();

    Mockito.when(verificationTokenRepository.findByToken(request))
            .thenReturn(Optional.empty());

    var response = Assertions.assertThrows(NotFoundException.class,
            () -> verificationService.findByToken(request));

    Assertions.assertEquals(ApplicationExceptionHandler.NOT_FOUND, response.getErrorCode());
    Assertions.assertEquals(String.format("token %s not found", request), response.getMessage());
  }

}
