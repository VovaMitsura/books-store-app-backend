package com.example.booksstoreappbackend.security;

import com.example.booksstoreappbackend.exception.ApplicationExceptionHandler;
import com.example.booksstoreappbackend.exception.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 * Rejects every unauthenticated request with an error code 401 sent back to the client.
 */
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

  /**
   * This is invoked when user tries to access a secured REST resource
   * without supplying any credentials.
   *
   * @param request       - http request.
   * @param response      - http response.
   * @param authException - exception occured while authorization.
   * @throws IOException .
   */
  @Override
  public void commence(HttpServletRequest request, HttpServletResponse response,
                       AuthenticationException authException)
          throws IOException {
    var errorResponse = new ErrorResponse(LocalDateTime.now(), HttpStatus.UNAUTHORIZED.toString(), ApplicationExceptionHandler.NO_PERMISSION,
            "Have not provided any credentials");

    var mapper = new ObjectMapper();
    mapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());

    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.getWriter().write(mapper.writeValueAsString(errorResponse));
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
  }
}
