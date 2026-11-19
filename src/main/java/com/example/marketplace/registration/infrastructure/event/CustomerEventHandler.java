package com.example.marketplace.registration.infrastructure.event;

import com.example.marketplace.common.infrastructure.event.dto.CustomerCreated;
import com.example.marketplace.registration.infrastructure.persistence.entity.Customer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.rest.core.annotation.HandleAfterCreate;
import org.springframework.data.rest.core.annotation.HandleAfterDelete;
import org.springframework.data.rest.core.annotation.HandleAfterSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RepositoryEventHandler(Customer.class)
public class CustomerEventHandler {

    private final ApplicationEventPublisher publisher;

    public CustomerEventHandler(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    @HandleAfterCreate
    public void handleAfterCreate(Customer customer) {
        log.warn("CustomerEventHandler#handleAfterCreate executado com sucesso!");
        publisher.publishEvent(new CustomerCreated(customer.getId().toString(), customer.getFirstName()));
    }

    @HandleAfterSave
    public void handleAfterSave(Customer customer) {
        log.warn("CustomerEventHandler#handleAfterSave executado com sucesso!");
    }

    @HandleAfterDelete
    public void handleAfterDelete(Customer customer) {
        log.warn("CustomerEventHandler#handleAfterDelete executado com sucesso!");
    }
}