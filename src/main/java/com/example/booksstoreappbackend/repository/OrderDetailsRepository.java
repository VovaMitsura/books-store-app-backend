package com.example.booksstoreappbackend.repository;

import com.example.booksstoreappbackend.model.OrderDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public interface OrderDetailsRepository extends JpaRepository<OrderDetails, UUID> {
}