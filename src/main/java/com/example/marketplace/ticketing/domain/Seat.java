package com.example.marketplace.ticketing.domain;

import lombok.Getter;

import java.util.UUID;

@Getter
public class Seat {

    private UUID id;
    private SeatId correlationId;

    public Seat(SeatId correlationId) {
        this.id = UUID.randomUUID();
        this.correlationId = correlationId;
    }

}
