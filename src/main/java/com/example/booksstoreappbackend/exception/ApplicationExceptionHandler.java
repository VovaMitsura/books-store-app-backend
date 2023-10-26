package com.example.booksstoreappbackend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

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
  public static final String BAD_REQUEST = "bad_request";
  public static final String PAYMENT_EXCEPTION = "payment_exception";
  public static final String QUANTITY_CONFLICT = "quantity_conflict";


  @ResponseBody
  @ResponseStatus(value = HttpStatus.CONFLICT)
  @ExceptionHandler(EntityConflictException.class)
  public ErrorResponse handleEntityConflictException(EntityConflictException exception) {
    return new ErrorResponse(LocalDateTime.now(),
            HttpStatus.CONFLICT.toString(),
            exception.getErrorCode(),
            exception.getMessage());
  }

  @ResponseBody
  @ResponseStatus(value = HttpStatus.BAD_REQUEST)
  @ExceptionHandler(MailingException.class)
  public ErrorResponse handleMailingException(MailingException exception) {
    return new ErrorResponse(LocalDateTime.now(),
            HttpStatus.BAD_REQUEST.toString(),
            exception.getErrorCode(),
            exception.getMessage());
  }

  @ResponseBody
  @ResponseStatus(value = HttpStatus.NOT_FOUND)
  @ExceptionHandler(NotFoundException.class)
  public ErrorResponse handleNotFoundException(NotFoundException exception) {
    return new ErrorResponse(LocalDateTime.now(),
            HttpStatus.NOT_FOUND.toString(),
            exception.getErrorCode(),
            exception.getMessage());
  }

  @ResponseBody
  @ResponseStatus(value = HttpStatus.PAYMENT_REQUIRED)
  @ExceptionHandler(PaymentException.class)
  public ErrorResponse handlePaymentException(PaymentException exception) {
    return new ErrorResponse(LocalDateTime.now(),
            HttpStatus.PAYMENT_REQUIRED.toString(),
            exception.getErrorCode(),
            exception.getMessage());
  }

  @ResponseBody
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(MethodArgumentNotValidException.class)
  protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
    var details = getErrorMap(ex);

    return ResponseEntity.badRequest().
            body(new ErrorResponse(LocalDateTime.now(),
                    HttpStatus.BAD_REQUEST.toString(),
                    BAD_REQUEST,
                    details.toString()));
  }

  private Map<String, String> getErrorMap(MethodArgumentNotValidException ex) {
    var result = new HashMap<String, String>();

    for (FieldError error : ex.getBindingResult().getFieldErrors()) {
      result.put(error.getField(), error.getDefaultMessage());
    }
    return result;
  }

}
