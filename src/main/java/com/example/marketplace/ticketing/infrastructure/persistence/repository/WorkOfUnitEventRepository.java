package com.example.marketplace.ticketing.infrastructure.persistence.repository;

import com.example.marketplace.ticketing.domain.*;
import com.example.marketplace.ticketing.infrastructure.persistence.entity.SeatLock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Slf4j
@Repository
public class WorkOfUnitEventRepository implements EventRepository {

    private final EventCrudRepository eventCrudRepository;
    private final RedisSeatLockRepository redisSeatLockRepository;

    public WorkOfUnitEventRepository(EventCrudRepository eventCrudRepository, RedisSeatLockRepository redisSeatLockRepository) {
        this.eventCrudRepository = eventCrudRepository;
        this.redisSeatLockRepository = redisSeatLockRepository;
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

    @Override
    public boolean existsSeat(EventId eventId, SeatId seatId) {
        return eventCrudRepository.existsByCorrelationIdAndSectors_Seats_CorrelationId(eventId.id(), seatId.id());
    }

    @Override
    public boolean tryLockSeat(EventId eventId, SeatId seatId, CustomerId customerId) {
        String lockId = eventId.id().toString() + ":" + seatId.id();
        if (redisSeatLockRepository.existsById(lockId)) {
            return false;
        }
        var lock = new SeatLock(lockId, customerId.id().toString(), Instant.now());
        redisSeatLockRepository.save(lock);
        return true;
    }
}
