package com.example.booksstoreappbackend.controller.dto.mapper;

import com.example.booksstoreappbackend.controller.dto.OrderDetailsResponseDto;
import com.example.booksstoreappbackend.controller.dto.OrderResponseDto;
import com.example.booksstoreappbackend.model.Order;
import com.example.booksstoreappbackend.model.OrderDetails;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderMapper {

  public OrderResponseDto toResponseDto(Order order, List<OrderDetails> orderDetails) {

    var orderDetailsResponseDto = orderDetails.stream()
            .map(orderDetail -> new OrderDetailsResponseDto(
                    orderDetail.getId(),
                    orderDetail.getBook().getTitle(),
                    orderDetail.getBook().getAuthor(),
                    orderDetail.getBook().getPrice(),
                    orderDetail.getQuantity()
            ))
            .toList();

    var totalPrice = orderDetails.stream()
            .map(orderDetail -> orderDetail.getBook().getPrice() * orderDetail.getQuantity())
            .reduce(0.0, Double::sum);

    return new OrderResponseDto(
            order.getId(),
            order.getStatus().name(),
            totalPrice,
            orderDetailsResponseDto
    );
  }

}
