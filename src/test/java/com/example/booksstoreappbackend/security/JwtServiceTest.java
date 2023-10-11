package com.example.booksstoreappbackend.security;

import com.example.booksstoreappbackend.model.User;
import com.example.booksstoreappbackend.util.UserFactory;
import io.jsonwebtoken.security.SignatureException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class JwtServiceTest {

  @Autowired
  JwtService jwtService;

  User user;

  @BeforeEach
  void setUp() {
    user = new UserFactory().createEntity();
  }

  @Test
  void should_return_generated_token() {
    Assertions.assertNotNull(jwtService.generateToken(user));
  }

  @Test
  void should_extract_user_email_from_jwt() {
    var jwt = jwtService.generateToken(user);
    var userEmail = jwtService.extractUserEmail(jwt);

    Assertions.assertEquals(user.getEmail(), userEmail);
  }

  @Test
  void should_return_true_for_valid_jwt() {
    var jwt = jwtService.generateToken(user);

    Assertions.assertTrue(jwtService.isTokenValid(jwt, user));
  }

  @Test
  void should_throw_exception_when_jwt_modified() {
    var jwt = jwtService.generateToken(user);
    jwt = jwt.substring(0, jwt.length() - 6);
    var modified = jwt + "eOm1Pk";

    Assertions.assertThrows(SignatureException.class, () ->
            jwtService.isTokenValid(modified, user));

  }
}
