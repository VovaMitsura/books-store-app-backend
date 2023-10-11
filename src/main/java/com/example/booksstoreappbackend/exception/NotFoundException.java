package com.example.booksstoreappbackend.exception;

import lombok.Getter;

/**
 * Represents exception, which occur when resource not found.
 */
@Getter
public class NotFoundException extends RuntimeException {

  private final String errorCode;

  public NotFoundException(String errorCode, String message) {
    super(message);
    this.errorCode = errorCode;
  }
}
