package com.example.booksstoreappbackend.service;

import com.example.booksstoreappbackend.controller.dto.OrderDto;
import com.example.booksstoreappbackend.controller.dto.OrderResponseDto;
import com.example.booksstoreappbackend.controller.dto.PaymentStatus;
import com.example.booksstoreappbackend.controller.dto.StripePaymentStatus;
import com.example.booksstoreappbackend.controller.dto.mapper.OrderMapper;
import com.example.booksstoreappbackend.exception.ApplicationExceptionHandler;
import com.example.booksstoreappbackend.exception.EntityConflictException;
import com.example.booksstoreappbackend.exception.NotFoundException;
import com.example.booksstoreappbackend.model.*;
import com.example.booksstoreappbackend.repository.OrderDetailsRepository;
import com.example.booksstoreappbackend.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.*;

@Service
@RequiredArgsConstructor
public class OrderService {

  public static final String MAIL_TEMPLATE_ORDER_IS_PAYED = "order_is_payed";
  public static final String MAIL_TEMPLATE_ORDER_IS_NOT_PAYED = "order_is_not_payed";

  private final OrderRepository orderRepository;
  private final BookService bookService;
  private final OrderDetailsRepository orderDetailsRepository;
  private final OrderMapper orderMapper;
  private final PaymentProvider paymentService;
  private final MailingService mailingService;

  private final Logger logger = LoggerFactory.getLogger(OrderService.class);

  public OrderResponseDto addProductToOrder(User user, OrderDto orderDto) {

    var order = new Order();
    var book = bookService.findByIdWithoutImage(orderDto.bookId());

    try {
      order = findOrderByUserEmailAndStatus(user.getEmail(), Order.Status.CREATED);
    } catch (NotFoundException e) {
      order.setCustomer(user);
      order.setStatus(Order.Status.CREATED);
    }

    var orderDetails = OrderDetails.builder()
            .order(order)
            .book(book)
            .quantity(orderDto.quantity())
            .build();

    order.setTotalAmount(order.getTotalAmount() + orderDetails.getTotalPrice());

    order = orderRepository.save(order);
    orderDetailsRepository.save(orderDetails);

    return orderMapper.toResponseDto(order, List.of(orderDetails));
  }

  public Order findOrderByUserEmailAndStatus(String email, Order.Status status) {
    return orderRepository.findByCustomerEmailAndStatus(email, status).orElseThrow(
            () -> new NotFoundException(ApplicationExceptionHandler.NOT_FOUND,
                    "Order not found for user with email: " + email + " and status: " + status)
    );
  }

  public PaymentStatus payForOrder(User user, CreditCard card) {
    var order = findOrderByUserEmailAndStatus(user.getEmail(), Order.Status.CREATED);

    return pay(card, order);
  }

  public PaymentStatus pay(CreditCard card, Order order) {

    ordersAmountNotGreaterThanProducts(order);
    var charge = paymentService.pay(card, order);
    PaymentStatus paymentStatus = new StripePaymentStatus(charge);

    order.setStatus(Order.Status.BOUGHT);
    order.setDate(new Timestamp(new Date().getTime()));
    orderRepository.save(order);

    Map<String, Object> payload = new HashMap<>();
    payload.put("order", order);
    payload.put("paymentStatus", paymentStatus);

    try {
      this.mailingService.sendForPay(order.getCustomer(), charge, order, order.getCustomer().getEmail(), paymentStatus.isSucceeded() ?
              MAIL_TEMPLATE_ORDER_IS_PAYED : MAIL_TEMPLATE_ORDER_IS_NOT_PAYED, payload);
    } catch (Exception e) {
      logger.error(String.format("Unable to send email to %s about %s with payment status %s",
              order.getCustomer().getEmail(), order, paymentStatus));
    }

    return paymentStatus;
  }

  private void ordersAmountNotGreaterThanProducts(Order order) {
    List<OrderDetails> orderDetails = order.getOrderDetails();

    for (OrderDetails details : orderDetails) {
      Book orderedProduct = details.getBook();
      Book productInMarket = bookService.findByIdWithoutImage(orderedProduct.getId());

      if (details.getQuantity() > productInMarket.getQuantity()) {
        throw new EntityConflictException(ApplicationExceptionHandler.QUANTITY_CONFLICT,
                String.format("There are not so quantity [%d] goods [%s] " + "in market",
                        orderedProduct.getQuantity(), orderedProduct.getTitle()));
      }
    }
  }

  public void deleteCustomerOrder(User customer, UUID orderId) {
    var order = orderRepository.findById(orderId).orElseThrow(
            () -> new NotFoundException(ApplicationExceptionHandler.NOT_FOUND,
                    "Order not found for user with email: " + customer.getEmail() + " and id: " + orderId)
    );

    if (!order.getCustomer().getEmail().equals(customer.getEmail())) {
      throw new EntityConflictException(ApplicationExceptionHandler.NOT_FOUND,
              "Order with id: " + orderId + " not found for user with email: " + customer.getEmail());
    }

    orderRepository.delete(order);
  }

  public OrderResponseDto getCustomerCreatedOrders(User customer) {
    var order = findOrderByUserEmailAndStatus(customer.getEmail(), Order.Status.CREATED);
    return orderMapper.toResponseDto(order, order.getOrderDetails());
  }
}
