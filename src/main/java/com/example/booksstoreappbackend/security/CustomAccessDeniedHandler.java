package com.example.booksstoreappbackend.security;

import com.example.booksstoreappbackend.exception.ApplicationExceptionHandler;
import com.example.booksstoreappbackend.exception.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;


/**
 * Rejects every authenticated request without authorities and send
 * an error code 403 sent back to the client.
 */
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
  /**
   * This is invoked when user tries to access a secured REST
   * resource without required authorities.
   *
   * @param request               - http request.
   * @param response              - htpp response.
   * @param accessDeniedException - exception, thrown when user tries
   *                              to access resources, which are not available to him.
   * @throws IOException .
   */
  @Override
  public void handle(HttpServletRequest request,
                     HttpServletResponse response,
                     AccessDeniedException accessDeniedException)
          throws IOException {
    var errorResponse = new ErrorResponse(ApplicationExceptionHandler.NO_PERMISSION,
            "Access Denied: have not the required role to access this resource.");

    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.getWriter().write(new ObjectMapper().writeValueAsString(errorResponse));
    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
  }
}
