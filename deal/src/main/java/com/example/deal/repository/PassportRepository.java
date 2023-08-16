package com.example.deal.repository;

import com.example.deal.dto.jsonb.Passport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PassportRepository extends JpaRepository<Passport, Long> {
}
