package com.example.marketplace.catalog.domain.entity.id;

import java.util.UUID;

public record EventId(UUID id) {

    public EventId {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
    }

    public EventId() {
        this(UUID.randomUUID());
    }
}
