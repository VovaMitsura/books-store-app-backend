package com.example.booksstoreappbackend.controller;

import com.example.booksstoreappbackend.controller.dto.PaymentRequestDto;
import com.example.booksstoreappbackend.controller.dto.PaymentStatus;
import com.example.booksstoreappbackend.controller.dto.StripePaymentResponseDto;
import com.example.booksstoreappbackend.controller.dto.StripePaymentStatus;
import com.example.booksstoreappbackend.model.Order;
import com.example.booksstoreappbackend.security.util.UserPrincipalUtil;
import com.example.booksstoreappbackend.service.OrderService;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/pay")
@PreAuthorize("hasAnyAuthority('CUSTOMER')")
@RequiredArgsConstructor
public class PaymentController {

  private final OrderService orderService;

  @PostMapping()
  public ResponseEntity<StripePaymentResponseDto> payForOrder(@Valid @RequestBody PaymentRequestDto paymentRequest) {
    var user = UserPrincipalUtil.extractUserPrinciple();

    var paymentStatus = orderService.payForOrder(user, paymentRequest.creditCard());

    var response = new StripePaymentResponseDto((StripePaymentStatus) paymentStatus,
            String.format("Payment is %s", paymentStatus.isSucceeded() ? "succeed" : "fail"));

    return ResponseEntity.ok(response);
  }

}
