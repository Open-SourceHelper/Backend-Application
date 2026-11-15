package com.neurosync.kinemo.child.interfaces.rest;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice(assignableTypes = ChildController.class)
public class ChildStatusHandler {
    @ExceptionHandler(ResponseStatusException.class)
    ProblemDetail status(ResponseStatusException error) { return error.getBody(); }
}
