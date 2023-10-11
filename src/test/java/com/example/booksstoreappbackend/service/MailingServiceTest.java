package com.example.booksstoreappbackend.service;

import freemarker.template.Configuration;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class MailingServiceTest {

  @Autowired
  Configuration configuration;

  @Test
  @SneakyThrows
  void should_return_ftl_templates(){
    Assertions.assertNotNull(configuration.getTemplate(AuthenticationService.CONFIRMATION + ".ftl"));
  }

}
