package com.example.booksstoreappbackend.controller;

import com.example.booksstoreappbackend.controller.dto.AuthenticationRequest;
import com.example.booksstoreappbackend.controller.dto.AuthenticationResponse;
import com.example.booksstoreappbackend.controller.dto.RegisterRequest;
import com.example.booksstoreappbackend.exception.ApplicationExceptionHandler;
import com.example.booksstoreappbackend.exception.ErrorResponse;
import com.example.booksstoreappbackend.model.User;
import com.example.booksstoreappbackend.model.VerificationToken;
import com.example.booksstoreappbackend.repository.UserRepository;
import com.example.booksstoreappbackend.repository.VerificationTokenRepository;
import com.example.booksstoreappbackend.security.JwtAuthenticationFilter;
import com.example.booksstoreappbackend.security.JwtService;
import com.example.booksstoreappbackend.service.MailingService;
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
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.UUID;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Sql(scripts = "/sql/delete.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class AuthenticationControllerTest {

  final String url = "/api/v1/auth";

  @Autowired
  WebApplicationContext applicationContext;
  @Autowired
  ObjectMapper objectMapper;
  @Autowired
  JwtAuthenticationFilter authenticationFilter;
  @Autowired
  UserRepository userRepository;
  @Autowired
  VerificationTokenRepository verificationTokenRepository;
  @Autowired
  JwtService jwtService;

  @MockBean
  MailingService mailingService;

  MockMvc mockMvc;
  User user;

  @BeforeEach
  void setup() {
    this.mockMvc = MockMvcBuilders.webAppContextSetup(applicationContext)
            .apply(SecurityMockMvcConfigurers.springSecurity())
            .build();

    objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
    user = new UserFactory().createEntity();
  }

  @Test
  void should_register_new_not_confirmed_user() throws Exception {

    var request = RegisterRequest.builder()
            .firstname(user.getFirstName())
            .lastname(user.getLastName())
            .email(user.getEmail())
            .password(user.getPassword())
            .role(user.getRole().toString());

    var mvcResult = this.mockMvc.perform(MockMvcRequestBuilders.post(url + "/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andReturn();

    var response = objectMapper.readValue(mvcResult.getResponse().getContentAsString(),
            HashMap.class);
    var valInDb = userRepository.findByEmail(user.getEmail())
            .orElseThrow();

    Assertions.assertEquals(String.format("Thanks for registering, please confirm your account %s",
            user.getEmail()), response.get("message"));
    Assertions.assertEquals(user.getEmail(), valInDb.getEmail());
    Assertions.assertEquals(user.getFirstName(), valInDb.getFirstName());
    Assertions.assertEquals(user.getLastName(), valInDb.getLastName());
    Assertions.assertFalse(valInDb.getConfirmed());
    Assertions.assertFalse(valInDb.isEnabled());
  }

  @Test
  void should_throw_exception_when_user_confirmed() throws Exception {
    userRepository.save(user);

    var request = RegisterRequest.builder()
            .firstname(user.getFirstName())
            .lastname(user.getLastName())
            .email(user.getEmail())
            .password(user.getPassword())
            .role(user.getRole().toString());

    var mvcResult = this.mockMvc.perform(MockMvcRequestBuilders.post(url + "/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
            .andExpect(MockMvcResultMatchers.status().isConflict())
            .andReturn();

    var response = objectMapper.readValue(mvcResult.getResponse().getContentAsString(),
            ErrorResponse.class);

    Assertions.assertEquals(ApplicationExceptionHandler.DUPLICATE_ENTRY, response.getErrorCode());
    Assertions.assertEquals(String.format("User with email %s already exists",
            user.getEmail()), response.getErrorMessage());

  }

  @Test
  void should_confirm_registered_account() throws Exception {
    user.setConfirmed(false);
    user = userRepository.save(user);

    var verToken = VerificationToken.builder()
            .expiryDate(new Timestamp(System.currentTimeMillis() + 1000 * 60))
            .id(UUID.randomUUID())
            .token(UUID.randomUUID().toString())
            .user(user)
            .build();

    verificationTokenRepository.save(verToken);

    var mvcResponse = this.mockMvc.perform(MockMvcRequestBuilders.get(
                            url + "/confirmation?token=" + verToken.getToken())
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andReturn();

    var response = objectMapper.readValue(mvcResponse.getResponse().getContentAsString(),
            AuthenticationResponse.class);

    Assertions.assertNotNull(response.token());
    Assertions.assertEquals(user.getEmail(), jwtService.extractUserEmail(response.token()));
  }

  @Test
  void should_throw_exception_when_token_expired() throws Exception {
    user.setConfirmed(false);
    user = userRepository.save(user);

    var verToken = VerificationToken.builder()
            .expiryDate(new Timestamp(System.currentTimeMillis() - 1000 * 60))
            .id(UUID.randomUUID())
            .token(UUID.randomUUID().toString())
            .user(user)
            .build();

    verificationTokenRepository.save(verToken);

    var mvcResponse = this.mockMvc.perform(MockMvcRequestBuilders.get(
                            url + "/confirmation?token=" + verToken.getToken())
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isConflict())
            .andReturn();

    var response = objectMapper.readValue(mvcResponse.getResponse().getContentAsString(),
            ErrorResponse.class);

    Assertions.assertEquals(ApplicationExceptionHandler.TOKEN_EXCEPTION, response.getErrorCode());
    Assertions.assertEquals(String.format("Your verification token has expired %s", verToken.getExpiryDate()),
            response.getErrorMessage());
  }

  @Test
  void should_authenticate_confirmed_user() throws Exception {
    user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
    user = userRepository.save(user);
    user.setPassword("123");

    var request = AuthenticationRequest.builder()
            .email(user.getEmail())
            .password(user.getPassword())
            .build();

    var mvcResponse = this.mockMvc.perform(MockMvcRequestBuilders.post(
                            url + "/authenticate")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andReturn();

    var response = objectMapper.readValue(mvcResponse.getResponse().getContentAsString(),
            AuthenticationResponse.class);

    Assertions.assertEquals(user.getEmail(), jwtService.extractUserEmail(response.token()));
  }

  @Test
  void should_throw_exception_when_account_is_enabled() throws Exception {
    user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
    user.setConfirmed(false);

    user = userRepository.save(user);

    user.setPassword("123");

    var request = AuthenticationRequest.builder()
            .email(user.getEmail())
            .password(user.getPassword())
            .build();

    var mvcResponse = this.mockMvc.perform(MockMvcRequestBuilders.post(
                            url + "/authenticate")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
            .andExpect(MockMvcResultMatchers.status().isConflict())
            .andReturn();

    var response = objectMapper.readValue(mvcResponse.getResponse().getContentAsString(),
            ErrorResponse.class);

    Assertions.assertEquals(ApplicationExceptionHandler.NOT_FOUND, response.getErrorCode());
    Assertions.assertEquals( String.format("User %s not registered or email not confirmed",
            user.getEmail()), response.getErrorMessage());
  }
}