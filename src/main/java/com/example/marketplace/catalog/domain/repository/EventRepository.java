package com.example.marketplace.catalog.domain.repository;

import com.example.marketplace.catalog.domain.entity.Event;

import java.util.List;

public interface EventRepository {
    List<Event> findAll();
}
