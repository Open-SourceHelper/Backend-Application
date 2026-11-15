package com.neurosync.kinemo.infrastructure.config;

import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@Profile("evaluation")
class EvaluationHealth {
    private final JdbcTemplate jdbc;

    EvaluationHealth(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @GetMapping("/health")
    Map<String, String> health() {
        jdbc.queryForObject("SELECT 1", Integer.class);
        return Map.of("status", "ok");
    }
}
