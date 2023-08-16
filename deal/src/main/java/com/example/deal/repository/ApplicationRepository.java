package com.example.deal.repository;

import com.example.deal.dto.entity.Application;
import com.example.deal.dto.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplicationRepository extends JpaRepository<Application, Long> {
    Application findApplicationByClient(Client client);
    Application findApplicationById(Long id);
}
