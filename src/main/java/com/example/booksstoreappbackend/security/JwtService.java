package com.example.booksstoreappbackend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

/**
 * Util class for creating, parsing and validating jwt token.
 */
@Service
public class JwtService {

  private static final long TOKEN_VALIDITY = 5 * 60 * 60L;
  private final String secretKey;

  public JwtService(@Value("${jwt.secret}") String secretKey) {
    this.secretKey = secretKey;
  }

  /**
   * Retrive user email from token.
   *
   * @param token - jwt.
   * @return user email.
   */
  public String extractUserEmail(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  /**
   * Retrive user credentials from token.
   *
   * @param token           - jwt
   * @param claimsTfunction - lambda function, which applied to specified claims.
   * @param <T>             - field, which must be retrieved.
   * @return token claim credential.
   */
  public <T> T extractClaim(String token, Function<Claims, T> claimsTfunction) {
    final Claims claims = extractAllClaims(token);
    return claimsTfunction.apply(claims);
  }

  /**
   * Retrieve all claims from jwt.
   *
   * @param token - jwt.
   * @return - jwt credentials.
   */
  public Claims extractAllClaims(String token) {
    return Jwts.parserBuilder()
            .setSigningKey(getSignKey())
            .build()
            .parseClaimsJws(token)
            .getBody();
  }

  /**
   * Generate jwt based on user email.
   *
   * @param userDetails - Authentication interface, which contains user email.
   * @return string jwt representation.
   */
  public String generateToken(UserDetails userDetails) {
    return generateToken(new HashMap<>(), userDetails);
  }

  /**
   * Overloaded method, wich accept additional credentials.
   *
   * @param extraClaims - extra user credentials (role, activation status, etc.).
   * @param userDetails - Authentication interface, which contains user email.
   * @return string jwt representation.
   */
  public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
    return Jwts.builder()
            .setClaims(extraClaims)
            .setSubject(userDetails.getUsername())
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(new Date(System.currentTimeMillis() + TOKEN_VALIDITY * 1000))
            .signWith(getSignKey(), SignatureAlgorithm.HS256)
            .compact();
  }

  /**
   * Check whether token is valid. If provided jwt contains the same user, as saved in database,
   * and it is not expired - return true;
   *
   * @param token       - jwt;
   * @param userDetails - Authentication interface, which contains user email.
   * @return boolean value whether token is valid or not.
   */
  public boolean isTokenValid(String token, UserDetails userDetails) {
    final String userEmail = extractUserEmail(token);
    return userEmail.equals(userDetails.getUsername()) && !isTokenExpired(token);
  }

  /**
   * Check whether token is expired or not.
   *
   * @param token - jwt
   * @return true if token is still non expired.
   */
  private boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());
  }

  /**
   * Retrieve token expiration date.
   *
   * @param token - jwt.
   * @return - date of expiration.
   */
  private Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }

  /**
   * Generate Key object, with hmacSha algorithm.
   *
   * @return - key instance.
   */
  private Key getSignKey() {
    byte[] keyBytes = Decoders.BASE64.decode(secretKey);
    return Keys.hmacShaKeyFor(keyBytes);
  }

}
