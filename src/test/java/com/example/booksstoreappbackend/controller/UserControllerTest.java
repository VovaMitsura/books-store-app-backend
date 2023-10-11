package com.example.booksstoreappbackend.controller;

import com.example.booksstoreappbackend.model.Role;
import com.example.booksstoreappbackend.model.User;
import com.example.booksstoreappbackend.repository.UserRepository;
import com.example.booksstoreappbackend.security.JwtAuthenticationFilter;
import com.example.booksstoreappbackend.security.JwtService;
import com.example.booksstoreappbackend.service.UserService;
import com.example.booksstoreappbackend.util.EntityFactory;
import com.example.booksstoreappbackend.util.UserFactory;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Sql(scripts = "/sql/delete.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class UserControllerTest {

  final String url = "/api/v1/me";
  static final String TOKEN_PREFIX = "Bearer ";
  static final String AUTH_HEADER = "Authorization";

  @Autowired
  WebApplicationContext applicationContext;
  @Autowired
  ObjectMapper objectMapper;
  @Autowired
  JwtAuthenticationFilter authenticationFilter;
  @Autowired
  UserRepository userRepository;
  @Autowired
  UserService userService;
  @Autowired
  JwtService jwtService;

  MockMvc mockMvc;
  User admin;
  String adminJwt;
  EntityFactory<User> userEntityFactory = new UserFactory();


  @BeforeEach
  void setup() {
    this.mockMvc = MockMvcBuilders.webAppContextSetup(applicationContext)
            .apply(SecurityMockMvcConfigurers.springSecurity())
            .build();

    objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
    admin = userEntityFactory.createEntity();
    admin.setRole(Role.ADMIN);
    adminJwt = jwtService.generateToken(admin);
  }

  @Test
  void should_return_user_info_for_registered_user() throws Exception {

    admin = userRepository.save(admin);

    var mockMvcResult = mockMvc.perform(MockMvcRequestBuilders.get(url)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(AUTH_HEADER, TOKEN_PREFIX + adminJwt))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andReturn();

    var response = objectMapper.readTree(mockMvcResult.getResponse().getContentAsString());
    var id = response.get("id").asText();
    var role = response.get("role").asText();
    var email = response.get("email").asText();
    var firstName = response.get("firstName").asText();
    var lastName = response.get("lastName").asText();
    var confirmed = response.get("confirmed").asText();

    Assertions.assertEquals(admin.getEmail(), email);
    Assertions.assertEquals(admin.getRole().toString(), role);
    Assertions.assertEquals(admin.getId().toString(), id);
    Assertions.assertEquals(admin.getFirstName(), firstName);
    Assertions.assertEquals(admin.getLastName(), lastName);
    Assertions.assertEquals(admin.getConfirmed().toString(), confirmed);
  }
}
