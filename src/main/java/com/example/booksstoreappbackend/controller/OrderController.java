package com.example.booksstoreappbackend.controller;

import com.example.booksstoreappbackend.controller.dto.OrderDto;
import com.example.booksstoreappbackend.controller.dto.OrderResponseDto;
import com.example.booksstoreappbackend.security.util.UserPrincipalUtil;
import com.example.booksstoreappbackend.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
@PreAuthorize("hasAnyAuthority('CUSTOMER', 'ADMIN')")
@RequiredArgsConstructor
public class OrderController {
  private final OrderService orderService;

  @PostMapping()
  public ResponseEntity<OrderResponseDto> createNewOrder(
          @Valid @RequestBody OrderDto orderRequest) {

    var response = orderService.addProductToOrder(
            UserPrincipalUtil.extractUserPrinciple(), orderRequest);

    return new ResponseEntity<>(response, HttpStatus.CREATED);
  }

  @DeleteMapping("/{orderId}")
  public ResponseEntity<?> deleteOrder(@PathVariable UUID orderId) {
    var customer = UserPrincipalUtil.extractUserPrinciple();
    orderService.deleteCustomerOrder(customer, orderId);
    return ResponseEntity.noContent().build();
  }

  @GetMapping()
  public ResponseEntity<?> getMyOrders() {
    var customer = UserPrincipalUtil.extractUserPrinciple();
    var response = orderService.getCustomerCreatedOrders(customer);
    return ResponseEntity.ok(response);
  }
}
