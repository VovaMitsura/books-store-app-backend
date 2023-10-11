package com.example.booksstoreappbackend.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

class UserTest {

  @Test
  void should_return_all_field_when_get_called() {
    var passwordEncoder = new BCryptPasswordEncoder();
    var encodePassword = passwordEncoder.encode("123");

    var user = User.builder()
            .firstName("John")
            .lastName("Doe")
            .email("johndoe@mail.moc")
            .role(Role.CUSTOMER)
            .password(encodePassword)
            .confirmed(true)
            .build();

    Assertions.assertNotNull(user);
    Assertions.assertEquals("John", user.getFirstName());
    Assertions.assertEquals("Doe", user.getLastName());
    Assertions.assertEquals("johndoe@mail.moc", user.getEmail());
    Assertions.assertEquals(Role.CUSTOMER, user.getRole());
    Assertions.assertEquals(encodePassword, user.getPassword());
    Assertions.assertEquals(true, user.getConfirmed());
    Assertions.assertEquals(user.isEnabled(), user.getConfirmed());
  }

}
