package com.example.booksstoreappbackend.service;

import com.example.booksstoreappbackend.exception.ApplicationExceptionHandler;
import com.example.booksstoreappbackend.exception.EntityConflictException;
import com.example.booksstoreappbackend.exception.NotFoundException;
import com.example.booksstoreappbackend.model.User;
import com.example.booksstoreappbackend.model.VerificationToken;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.example.booksstoreappbackend.repository.VerificationTokenRepository;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.UUID;


/**
 * Verification Service for confirmation user creation.
 */
@Service
@RequiredArgsConstructor
public class VerificationService {

  private static final int EXPIRY_DATE = 24 * 60;

  private final VerificationTokenRepository verificationTokenRepository;

  /**
   * Save ver token by generating unique value and expiration date.
   *
   * @param user - user, who plans to register account.
   * @return created verification token.
   */
  public VerificationToken save(User user) {
    var token = UUID.randomUUID().toString();
    var expiryDate = calculateExpirationDate();
    var verToken = VerificationToken.builder()
            .token(token)
            .user(user)
            .expiryDate(expiryDate)
            .build();
    return verificationTokenRepository.save(verToken);
  }

  /**
   * Update expiry date and token value in existing token.
   *
   * @param user - existing user with verification token.
   * @return - updated token.
   */
  public VerificationToken update(User user) {
    var existingToken =
            verificationTokenRepository.findByUserId(user.getId())
                    .orElseThrow(() ->
                            new EntityConflictException(
                                    ApplicationExceptionHandler.NOT_FOUND,
                                    String.format("Token for user %s not found", user.getEmail())));
    var token = UUID.randomUUID().toString();
    var expiryDate = calculateExpirationDate();
    var varToken = VerificationToken.builder()
            .token(token)
            .user(user)
            .expiryDate(expiryDate)
            .id(existingToken.getId())
            .build();

    return verificationTokenRepository.save(varToken);
  }

  /**
   * Find confirmation token entity by provided token.
   * If not found, throws exception.
   *
   * @param token - confirmation token.
   * @return - verification token entity.
   */
  public VerificationToken findByToken(String token) {
    return verificationTokenRepository.findByToken(token)
            .orElseThrow(() -> new NotFoundException(ApplicationExceptionHandler.NOT_FOUND,
                    String.format("token %s not found", token)));
  }

  private Timestamp calculateExpirationDate() {
    var cal = Calendar.getInstance();
    cal.add(Calendar.MINUTE, VerificationService.EXPIRY_DATE);
    return new Timestamp(cal.getTime().getTime());
  }
}
