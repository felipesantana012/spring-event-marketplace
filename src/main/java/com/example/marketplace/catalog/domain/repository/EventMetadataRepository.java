package com.example.marketplace.catalog.domain.repository;

import com.example.marketplace.catalog.domain.entity.EventMetadata;
import com.example.marketplace.catalog.domain.entity.id.EventId;

import java.util.Optional;

public interface EventMetadataRepository {

    Optional<EventMetadata> findByEventId(EventId eventId);
}
