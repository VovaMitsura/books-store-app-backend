package com.example.booksstoreappbackend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Controller for handling exception in application.
 */
@ControllerAdvice
public class ApplicationExceptionHandler {

  public static final String DUPLICATE_ENTRY = "duplicate_entry";
  public static final String NOT_FOUND = "not_found";
  public static final String MAILING_EXCEPTION = "mailing_exception";
  public static final String TOKEN_EXCEPTION = "token_exception";
  public static final String NO_PERMISSION = "no_permission";

  @ResponseBody
  @ResponseStatus(value = HttpStatus.CONFLICT)
  @ExceptionHandler(EntityConflictException.class)
  public ErrorResponse handleEntityConflictException(EntityConflictException exception) {
    return new ErrorResponse(exception.getErrorCode(), exception.getMessage());
  }

  @ResponseBody
  @ResponseStatus(value = HttpStatus.BAD_REQUEST)
  @ExceptionHandler(MailingException.class)
  public ErrorResponse handleMailingException(MailingException exception) {
    return new ErrorResponse(exception.getErrorCode(), exception.getMessage());
  }

  @ResponseBody
  @ResponseStatus(value = HttpStatus.NOT_FOUND)
  @ExceptionHandler(NotFoundException.class)
  public ErrorResponse handleNotFoundException(NotFoundException exception) {
    return new ErrorResponse(exception.getErrorCode(), exception.getMessage());
  }

}
