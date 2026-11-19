package com.example.marketplace;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.List;

@SpringBootApplication
@EnableAsync
public class MarketplaceApplication {

	public static void main(String[] args) {

		SpringApplication.run(MarketplaceApplication.class, args);

	}

}
