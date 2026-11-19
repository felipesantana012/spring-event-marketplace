package com.example.marketplace.registration.domain;

import java.util.List;

public interface CustomerRepository {

    Customer save(Customer customer);
    List<Customer> findAll();

}
