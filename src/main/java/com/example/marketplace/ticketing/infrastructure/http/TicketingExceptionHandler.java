package com.example.marketplace.ticketing.infrastructure.http;

import com.example.marketplace.ticketing.domain.SeatAlreadyReservedException;
import com.example.marketplace.ticketing.domain.SeatNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice(basePackages = "com.example.marketplace.ticketing.infrastructure.http")
public class TicketingExceptionHandler {

    @ExceptionHandler(SeatNotFoundException.class)
    public ProblemDetail handleSeatNotFound(SeatNotFoundException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setTitle("Seat Not Found");
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    @ExceptionHandler(SeatAlreadyReservedException.class)
    public ProblemDetail handleSeatAlreadyReserved(SeatAlreadyReservedException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.LOCKED, ex.getMessage());
        problem.setTitle("Seat Already Reserved");
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }
}