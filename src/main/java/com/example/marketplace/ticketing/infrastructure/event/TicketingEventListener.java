package com.example.marketplace.ticketing.infrastructure.event;

import com.example.marketplace.common.infrastructure.event.dto.CustomerCreated;
import com.example.marketplace.common.infrastructure.event.dto.EventUpdated;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TicketingEventListener {

    @EventListener
    @Async
    public void handle(CustomerCreated event){
        log.info("CustomerCreated received {}", event);
    }

    @EventListener
    @Async
    public void handle(EventUpdated event){
        log.info("EventUpdated received {}", event);
    }
}
