package com.example.deal.repository;

import com.example.deal.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, Long> {
    List<Client> findAll();

    Optional<Client> findClientByFirstName(String firstName);
}
