package com.example.booksstoreappbackend.controller;


import com.example.booksstoreappbackend.controller.dto.GrantDto;
import com.example.booksstoreappbackend.exception.ApplicationExceptionHandler;
import com.example.booksstoreappbackend.exception.ErrorResponse;
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
class AdminControllerTest {

  final String url = "/api/v1/admins";
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
  void should_update_user_role() throws Exception {
    var generalUser = userEntityFactory.createEntity();
    generalUser.setEmail("mock@mail.com");

    userRepository.save(admin);
    userRepository.save(generalUser);

    var request = GrantDto.builder()
            .role(Role.ADMIN)
            .email(generalUser.getEmail());

    var mockMvc = this.mockMvc.perform(MockMvcRequestBuilders.post(url)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
                    .header(AUTH_HEADER, TOKEN_PREFIX + adminJwt))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andReturn();

    var response = objectMapper.readValue(mockMvc.getResponse().getContentAsString(), GrantDto.class);

    Assertions.assertEquals(generalUser.getEmail(), response.email());
    Assertions.assertEquals(Role.ADMIN, response.role());
  }

  @Test
  void should_throw_exception_when_user_not_exists() throws Exception {
    userRepository.save(admin);

    var generalUser = userEntityFactory.createEntity();
    generalUser.setEmail("mock@mail.com");

    var request = GrantDto.builder()
            .role(Role.ADMIN)
            .email(generalUser.getEmail());

    var mockMvc = this.mockMvc.perform(MockMvcRequestBuilders.post(url)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
                    .header(AUTH_HEADER, TOKEN_PREFIX + adminJwt))
            .andExpect(MockMvcResultMatchers.status().isNotFound())
            .andReturn();

    var response = objectMapper.readValue(mockMvc.getResponse().getContentAsString(), ErrorResponse.class);
    var errorResponse = ErrorResponse.builder()
            .errorCode(ApplicationExceptionHandler.NOT_FOUND)
            .errorMessage(String.format("User with email %s not found", "mock@mail.com"))
            .build();

    Assertions.assertEquals(ApplicationExceptionHandler.NOT_FOUND, response.getErrorCode());
    Assertions.assertEquals(String.format("User with email %s not found", "mock@mail.com"),
            response.getErrorMessage());
    Assertions.assertEquals(errorResponse, response);
  }

  @Test
  void should_throw_exception_when_user_role_general() throws Exception {
    var generalUser = userEntityFactory.createEntity();
    generalUser.setEmail("mock@mail.com");
    userRepository.save(generalUser);

    var generalJwt = jwtService.generateToken(generalUser);

    var request = GrantDto.builder()
            .role(Role.ADMIN)
            .email(generalUser.getEmail());

    var mockMvc = this.mockMvc.perform(MockMvcRequestBuilders.post(url)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
                    .header(AUTH_HEADER, TOKEN_PREFIX + generalJwt))
            .andExpect(MockMvcResultMatchers.status().isForbidden())
            .andReturn();

    var response = objectMapper.readValue(mockMvc.getResponse().getContentAsString(),
            ErrorResponse.class);

    Assertions.assertEquals(ApplicationExceptionHandler.NO_PERMISSION,
            response.getErrorCode());
    Assertions.assertEquals(
            "Access Denied: have not the required role to access this resource.",
            response.getErrorMessage());
  }

  @Test
  void should_throw_exception_when_user_have_not_provide_any_credential() throws Exception {
    var request = GrantDto.builder()
            .role(Role.ADMIN)
            .email("mock@mail");

    var mockMvc = this.mockMvc.perform(MockMvcRequestBuilders.post(url)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
            .andExpect(MockMvcResultMatchers.status().isUnauthorized())
            .andReturn();

    var response = objectMapper.readValue(mockMvc.getResponse().getContentAsString(),
            ErrorResponse.class);

    Assertions.assertEquals(ApplicationExceptionHandler.NO_PERMISSION,
            response.getErrorCode());
    Assertions.assertEquals("Have not provided any credentials",
            response.getErrorMessage());
  }
}