package com.example.marketplace.ticketing.domain;

import lombok.Getter;

import java.util.UUID;

@Getter
public class Customer {

    private UUID id;
    private CustomerId correlationId;
    private String name;

    public Customer(CustomerId correlationId, String name) {
        this.id = UUID.randomUUID();
        this.correlationId = correlationId;
        this.name = name;
    }
}
