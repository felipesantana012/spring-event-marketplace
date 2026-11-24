package com.example.marketplace.ticketing.infrastructure.http.request;

import com.example.marketplace.ticketing.domain.SeatId;

public record SeatSelectionRequest(String id) {
    public SeatId toInput() {
        return new SeatId(id);
    }
}
