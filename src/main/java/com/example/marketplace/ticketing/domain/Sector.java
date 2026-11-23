package com.example.marketplace.ticketing.domain;


import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
public class Sector {

    private UUID id;
    private SectorId sectorId;
    private BigDecimal price;

    public Sector(SectorId sectorId, BigDecimal price) {
        this.id = UUID.randomUUID();
        this.sectorId = sectorId;
        this.price = price;
    }
}
