package com.example.phone_comparison_backend.repository;

import com.example.phone_comparison_backend.model.SearchTerm;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface SearchTermRepository extends JpaRepository<SearchTerm, Long> {
    Optional<SearchTerm> findByTerm(String term);
}
