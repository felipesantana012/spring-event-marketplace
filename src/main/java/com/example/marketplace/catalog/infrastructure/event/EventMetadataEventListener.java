package com.example.marketplace.catalog.infrastructure.event;

import com.example.marketplace.catalog.infrastructure.persistence.entity.EventMetadata;
import com.example.marketplace.common.infrastructure.event.dto.EventUpdated;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.AfterDeleteEvent;
import org.springframework.data.mongodb.core.mapping.event.AfterSaveEvent;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EventMetadataEventListener extends AbstractMongoEventListener<EventMetadata> {

    private final ApplicationEventPublisher publisher;

    public EventMetadataEventListener(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    @Override
    public void onAfterSave(AfterSaveEvent<EventMetadata> event){
        log.info("Event metadata save via onAfterSave: {}", event.getDocument());
        this.publisher.publishEvent(EventUpdated.from(event.getSource()));
    }

    @Override
    public void onAfterDelete(AfterDeleteEvent<EventMetadata> event){
        log.info("Event metadata delete via onAfterDelete: {}", event.getDocument());
    }

}
