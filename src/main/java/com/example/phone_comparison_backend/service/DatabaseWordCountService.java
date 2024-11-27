package com.example.phone_comparison_backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class DatabaseWordCountService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public int getDatabaseWordCount(String term) {
        String sql = "SELECT COUNT(*) FROM phones WHERE model LIKE ? OR company LIKE ?";
        String searchTerm = "%" + term + "%";
        return jdbcTemplate.queryForObject(sql, Integer.class, searchTerm, searchTerm);
    }
}