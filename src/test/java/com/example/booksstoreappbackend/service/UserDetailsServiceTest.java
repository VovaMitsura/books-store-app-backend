package com.example.booksstoreappbackend.service;

import com.example.booksstoreappbackend.model.Role;
import com.example.booksstoreappbackend.model.User;
import com.example.booksstoreappbackend.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Sql(scripts = "/sql/delete.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class UserDetailsServiceTest {

  @Autowired
  UserDetailsService userDetailsService;
  @Autowired
  UserRepository userRepository;

  @Test
  void should_find_registered_user() {
    var user = User.builder()
            .email("john@mail.com")
            .role(Role.ADMIN)
            .firstName("John")
            .lastName("Doe")
            .confirmed(true)
            .build();

    user = userRepository.save(user);

    var authorizedUser = userDetailsService.loadUserByUsername("john@mail.com");

    Assertions.assertEquals(user.getEmail(), authorizedUser.getUsername());
    Assertions.assertEquals(user.isEnabled(), authorizedUser.isEnabled());
    Assertions.assertEquals(user.getRole().toString(),
            authorizedUser.getAuthorities().toArray()[0].toString());
  }

  @Test
  void should_throw_exception_if_user_not_registered() {
    var exception = Assertions.assertThrows(UsernameNotFoundException.class, () ->
            userDetailsService.loadUserByUsername("john@mail.com"));

    Assertions.assertEquals(String.format("User with email: %s not found",
                    "john@mail.com"),
            exception.getMessage());
  }

}
