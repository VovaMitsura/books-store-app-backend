package com.example.booksstoreappbackend.service;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.example.booksstoreappbackend.model.User;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

/**
 * Service for mailing users, used freemarker templates
 * to adapting html.
 */
@Service
public class MailingService {

  private final JavaMailSender mailSender;
  private final Configuration freeMarkerConfig;
  private final Properties subjects;

  /**
   * Default constructor.
   *
   * @param mailSender - inject bean of MailSender.
   * @param freeMarkerConfig - freemarker bean, which loads templates.
   * @throws IOException .
   */
  public MailingService(JavaMailSender mailSender,
                        Configuration freeMarkerConfig) throws IOException {
    this.mailSender = mailSender;
    this.freeMarkerConfig = freeMarkerConfig;
    this.subjects = new Properties();
    this.subjects.load(this.getClass()
            .getClassLoader()
            .getResourceAsStream("mail-subjects.properties"));
  }

  /**
   * General method to for sending mails.
   *
   * @param user - receiver.
   * @param template - html template for message.
   * @param payloads - data for insertion in template.
   * @throws MessagingException .
   * @throws TemplateException .
   * @throws IOException .
   */
  public void send(User user, String template, Map<String, Object> payloads)
          throws MessagingException,
          TemplateException, IOException {
    this.send(user, user.getEmail(), template, payloads);
  }

  /**
   * Overloaded method which, send message to provided email.
   *
   * @param user - receiver.
   * @param emailToAddress - user email.
   * @param template - html template for message.
   * @param payloads - data for insertion in template.
   * @throws MessagingException .
   * @throws TemplateException .
   * @throws IOException .
   */
  public void send(User user, String emailToAddress, String template, Map<String, Object> payloads)
          throws MessagingException, IOException, TemplateException {
    Map<String, Object> content = new HashMap<>(payloads);
    content.put("customer", user);

    MimeMessage mailMessage = this.mailSender.createMimeMessage();
    MimeMessageHelper helper = new MimeMessageHelper(mailMessage, true);
    helper.setTo(emailToAddress);
    helper.setSubject(this.subjects.getProperty(template));

    Template tml = this.freeMarkerConfig.getTemplate(template + ".ftl");
    StringWriter writer = new StringWriter();
    tml.process(content, writer);
    helper.setText(writer.toString(), true);

    this.mailSender.send(mailMessage);
  }
}
