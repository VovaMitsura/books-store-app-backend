package com.example.booksstoreappbackend.exception;

import lombok.Getter;

/**
 * Represents errors, which occurs with entities.
 */
@Getter
public class EntityConflictException extends RuntimeException {

  private final String errorCode;

  public EntityConflictException(String errorCode, String message) {
    super(message);
    this.errorCode = errorCode;
  }


}
