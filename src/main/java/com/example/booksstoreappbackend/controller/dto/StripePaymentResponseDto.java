package com.example.booksstoreappbackend.controller.dto;

import com.example.booksstoreappbackend.exception.ApplicationExceptionHandler;
import com.example.booksstoreappbackend.exception.EntityConflictException;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;

import java.util.Map;

@Data
public class StripePaymentResponseDto {
  private String message;
  private Map<String, Object> chargeJson;

  public StripePaymentResponseDto(StripePaymentStatus paymentStatus, String message) {
    this.message = message;
    try {
      this.chargeJson = new ObjectMapper().readValue(paymentStatus.getCharge().toJson(), new TypeReference<>() {
      });
    } catch (JsonProcessingException e) {
      throw new EntityConflictException(ApplicationExceptionHandler.PAYMENT_EXCEPTION, "Error while parsing charge json") {
      };
    }
  }

  @JsonAnyGetter
  public Map<String, Object> getChargeJson() {
    return this.chargeJson;
  }
}
