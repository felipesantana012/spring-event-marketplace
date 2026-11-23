package com.example.marketplace.ticketing.infrastructure.persistence.repository;

import com.example.marketplace.ticketing.domain.Event;
import com.example.marketplace.ticketing.domain.EventRepository;
import com.example.marketplace.ticketing.domain.Seat;
import com.example.marketplace.ticketing.domain.Sector;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@Repository
public class PostgresEventRepository implements EventRepository {

    private final EventCrudRepository eventCrudRepository;

    public PostgresEventRepository(EventCrudRepository eventCrudRepository) {
        this.eventCrudRepository = eventCrudRepository;
    }

    @Override
    public void save(Event event) {
        log.info("Saving event {}", event);

        List<com.example.marketplace.ticketing.infrastructure.persistence.entity.Sector> sectors =
                event.getSeats().entrySet().stream()
                .map(entry -> {
                    Sector domainSector = entry.getKey();
                    List<Seat> domainSeats = entry.getValue();

                    var seats = domainSeats.stream()
                            .map(seat -> new com.example.marketplace.ticketing.infrastructure.persistence.entity.Seat(
                                    seat.getId(),
                                    seat.getCorrelationId().id()
                            ))
                            .toList();

                    return new com.example.marketplace.ticketing.infrastructure.persistence.entity.Sector(
                            domainSector.getId(),
                            domainSector.getSectorId().id(),
                            domainSector.getPrice(),
                            seats
                    );
                }).toList();

        var entity = new com.example.marketplace.ticketing.infrastructure.persistence.entity.Event(
                event.getId(),
                event.getCorrelationId().id(),
                sectors
        );
        eventCrudRepository.save(entity);
    }
}
