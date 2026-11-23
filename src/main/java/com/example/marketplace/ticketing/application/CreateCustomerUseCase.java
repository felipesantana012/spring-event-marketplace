package com.example.marketplace.ticketing.application;

import com.example.marketplace.common.infrastructure.event.dto.CustomerCreated;
import com.example.marketplace.ticketing.domain.Customer;
import com.example.marketplace.ticketing.domain.CustomerId;
import com.example.marketplace.ticketing.domain.CustomerRepository;
import org.springframework.stereotype.Service;

@Service
public class CreateCustomerUseCase {

    private final CustomerRepository customerRepository;

    public CreateCustomerUseCase(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public void execute(CustomerCreated event) {
        var customer = new Customer(new CustomerId(event.id()), event.name());
        customerRepository.save(customer);
    }

}
