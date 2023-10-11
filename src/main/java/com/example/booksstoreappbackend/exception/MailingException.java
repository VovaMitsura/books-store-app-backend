package com.example.booksstoreappbackend.exception;

import lombok.Getter;

/**
 * Represents exception, which occurs with mailing.
 */
@Getter
public class MailingException extends RuntimeException {
  private final String errorCode;

  public MailingException(String errorCode, String message) {
    super(message);
    this.errorCode = errorCode;
  }
}
