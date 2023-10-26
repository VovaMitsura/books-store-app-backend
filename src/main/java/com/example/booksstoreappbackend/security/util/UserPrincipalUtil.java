package com.example.booksstoreappbackend.security.util;

import com.example.booksstoreappbackend.model.User;
import org.springframework.security.core.context.SecurityContextHolder;

public class UserPrincipalUtil {

  public static User extractUserPrinciple() {
    return (User) SecurityContextHolder.getContext()
            .getAuthentication().getPrincipal();
  }

}
