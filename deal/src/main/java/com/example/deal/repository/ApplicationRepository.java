package com.example.deal.repository;

import com.example.deal.entity.Application;
import com.example.deal.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ApplicationRepository extends JpaRepository<Application, Long> {
    Optional<Application> findApplicationByClient(Client client);
}
