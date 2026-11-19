package com.example.marketplace.catalog.application;

import com.example.marketplace.catalog.application.dto.EventOutput;
import com.example.marketplace.catalog.domain.entity.Event;
import com.example.marketplace.catalog.domain.repository.EventMetadataRepository;
import com.example.marketplace.catalog.domain.repository.EventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class BrowseShowcaseUseCase {

    private Logger logger = LoggerFactory.getLogger(BrowseShowcaseUseCase.class);

    private final EventRepository eventRepository;
    private final EventEnricher eventEnricher;

    public BrowseShowcaseUseCase(EventRepository eventRepository, EventMetadataRepository eventMetadataRepository, EventEnricher eventEnricher) {
        this.eventRepository = eventRepository;
        this.eventEnricher = eventEnricher;
    }

    public List<EventOutput> execute(){
        var futures = eventRepository.findAll().stream().map(eventEnricher::enrich).toList();

        var events = futures.stream()
                .map(CompletableFuture::join)
                .map(EventOutput::from)
                .toList();

        logger.info("{} events enriched", events.size());
        return events;
    }


}
