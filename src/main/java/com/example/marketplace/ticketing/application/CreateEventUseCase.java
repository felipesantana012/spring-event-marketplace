package com.example.marketplace.ticketing.application;

import com.example.marketplace.common.infrastructure.event.dto.EventUpdated;
import com.example.marketplace.ticketing.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CreateEventUseCase {

    private final EventRepository eventRepository;

    public CreateEventUseCase(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public void execute(EventUpdated event) {
        Map<Sector, List<Seat>> seats = event.sectors().stream()
                .collect(Collectors.toMap(
                        sector -> new Sector(new SectorId(sector.id()), sector.price()),
                        sector -> sector.seats().stream()
                                .map(seatDto -> new Seat(new SeatId(seatDto.number())))
                                .toList()
                ));

        eventRepository.save(new Event(new EventId(event.id()), seats));
    }

}
