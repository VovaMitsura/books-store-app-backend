package com.example.booksstoreappbackend.service;

import com.example.booksstoreappbackend.model.CreditCard;
import com.example.booksstoreappbackend.model.Order;
import com.stripe.model.Charge;

public interface PaymentProvider {
  Charge pay(CreditCard card, Order order);
}
