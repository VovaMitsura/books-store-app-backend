package com.example.booksstoreappbackend.util;

import com.example.booksstoreappbackend.model.User;
import com.example.booksstoreappbackend.model.Role;

import java.util.UUID;

public class UserFactory implements EntityFactory<User> {

  @Override
  public User createEntity() {
    return User.builder()
            .id(UUID.randomUUID())
            .firstName("John")
            .lastName("Doe")
            .email("johndoe@mail.moc")
            .role(Role.CUSTOMER)
            .password("123")
            .confirmed(true)
            .build();
  }
}
