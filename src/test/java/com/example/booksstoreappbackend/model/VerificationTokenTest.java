package com.example.booksstoreappbackend.model;

import com.example.booksstoreappbackend.util.UserFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.util.UUID;

class VerificationTokenTest {

  @Test
  void should_return_user_when_token_created() {
    var factory = new UserFactory();
    var user = factory.createEntity();

    var vetToken = VerificationToken.builder()
            .token(UUID.randomUUID().toString())
            .user(user)
            .id(UUID.randomUUID())
            .expiryDate(new Timestamp(System.currentTimeMillis()))
            .build();

    Assertions.assertNotNull(vetToken);
    Assertions.assertEquals(user, vetToken.getUser());
  }

}
