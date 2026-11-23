package com.example.marketplace.ticketing.domain;

import lombok.Getter;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
public class Event {

    private UUID id;
    private EventId correlationId;
    private Map<Sector, List<Seat>> seats;

    public Event(EventId correlationId, Map<Sector, List<Seat>> seats) {
        this.id = UUID.randomUUID();
        this.correlationId = correlationId;
        this.seats = seats;
    }
}
