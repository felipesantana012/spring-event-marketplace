package com.example.marketplace.catalog.infrastructure.persistence.repository;

import com.example.marketplace.catalog.domain.entity.EventMetadata;
import com.example.marketplace.catalog.domain.entity.Seat;
import com.example.marketplace.catalog.domain.entity.Sector;
import com.example.marketplace.catalog.domain.entity.id.EventId;
import com.example.marketplace.catalog.domain.entity.id.SeatId;
import com.example.marketplace.catalog.domain.entity.id.SectorId;
import com.example.marketplace.catalog.domain.repository.EventMetadataRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository
public class MongoEventMetadataRepository implements EventMetadataRepository {

    private final EventMetadataEntityRepository eventMetadataEntityRepository;

    public MongoEventMetadataRepository(EventMetadataEntityRepository eventMetadataEntityRepository) {
        this.eventMetadataEntityRepository = eventMetadataEntityRepository;
    }

    @Override
    public Optional<EventMetadata> findByEventId(EventId eventId) {
        return eventMetadataEntityRepository.findByEventId(eventId.id())
                .map(MongoEventMetadataRepository::mapper);
    }

    private static EventMetadata mapper(com.example.marketplace.catalog.infrastructure.persistence.entity.EventMetadata eventMetadata) {
        var sectors = Optional.ofNullable(eventMetadata.getSectors())
                .orElseGet(List::of)
                .stream()
                .map(MongoEventMetadataRepository::mapper)
                .collect(Collectors.toMap(
                        sector -> sector.getId().name(),
                        Function.identity(),
                        (existing, replacement) -> existing
                ));

        var seats = Optional.ofNullable(eventMetadata.getSeats())
                .orElseGet(List::of)
                .stream()
                .map(MongoEventMetadataRepository::mapper)
                .filter(seat -> seat.getSectorId() != null && sectors.containsKey(seat.getSectorId().name()))
                .collect(Collectors.groupingBy(
                        seat -> sectors.get(seat.getSectorId().name())
                ));

        return new EventMetadata(
                eventMetadata.getEventDescription(),
                eventMetadata.getTechnicalRequirements(),
                seats
        );
    }

    private static Seat mapper(com.example.marketplace.catalog.infrastructure.persistence.entity.EventMetadata.Seat seat) {
        return new Seat(
                new SeatId(seat.getCode()), new SectorId(seat.getSectorName()));
    }

    private static Sector mapper(com.example.marketplace.catalog.infrastructure.persistence.entity.EventMetadata.Sector sector) {
        return new Sector(
                new SectorId(sector.getName()), sector.getPrice());
    }
}