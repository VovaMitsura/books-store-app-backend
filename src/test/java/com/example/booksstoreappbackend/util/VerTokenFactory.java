package com.example.booksstoreappbackend.util;


import com.example.booksstoreappbackend.model.VerificationToken;

import java.sql.Timestamp;
import java.util.UUID;

public class VerTokenFactory implements EntityFactory<VerificationToken>{
  @Override
  public VerificationToken createEntity() {
    return VerificationToken.builder()
            .token(UUID.randomUUID().toString())
            .id(UUID.randomUUID())
            .expiryDate(new Timestamp(System.currentTimeMillis()))
            .build();
  }
}
