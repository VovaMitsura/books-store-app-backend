package com.example.booksstoreappbackend.service;

import com.example.booksstoreappbackend.exception.ApplicationExceptionHandler;
import com.example.booksstoreappbackend.exception.NotFoundException;
import com.example.booksstoreappbackend.model.Role;
import com.example.booksstoreappbackend.model.User;
import com.example.booksstoreappbackend.repository.UserRepository;
import com.example.booksstoreappbackend.util.UserFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class UserServiceTest {

  @Autowired
  UserService userService;

  @MockBean
  UserRepository userRepository;

  User user = null;

  @BeforeEach
  void setUp(){
    user = new UserFactory().createEntity();
  }

  @Test
  void should_return_user_from_db_by_email(){
    Mockito.when(userRepository.findByEmail(user.getEmail()))
            .thenReturn(Optional.of(user));

    var response = userService.getUserByEmail(user.getEmail());

    Assertions.assertEquals(user, response);
  }

  @Test
  void should_throw_exception_when_user_not_exists(){
    Mockito.when(userRepository.findByEmail("mock@mail.com"))
            .thenReturn(Optional.empty());

    var exception = Assertions.assertThrows(NotFoundException.class, () ->
            userService.getUserByEmail("mock@mail.com"));

    Assertions.assertEquals(ApplicationExceptionHandler.NOT_FOUND, exception.getErrorCode());
    Assertions.assertEquals(String.format("User with email %s not found", "mock@mail.com"), exception.getMessage());
  }

  @Test
  void should_update_user_role_to_admin(){
    var updatedUser = new UserFactory().createEntity();
    updatedUser.setRole(Role.ADMIN);


    Mockito.when(userRepository.findByEmail(user.getEmail()))
            .thenReturn(Optional.of(user));
    Mockito.when(userRepository.save(Mockito.any(User.class )))
            .thenReturn(updatedUser);

    var response = userService.grantUserNewRole(user.getEmail(), Role.ADMIN);

    Assertions.assertEquals(response.email(), user.getEmail());
    Assertions.assertEquals(Role.ADMIN, response.role());
  }
}
