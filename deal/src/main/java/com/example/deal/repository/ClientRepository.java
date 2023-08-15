package com.example.deal.repository;

import com.example.deal.dto.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, Long> {
    Client findFirstById(Long sensorId);

    List<Client> findAll();

    Optional<Client> findById(Long id);

    Optional<Client> findByFirstName(String name);
}
