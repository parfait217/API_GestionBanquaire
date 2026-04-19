package com.example.banque.repository;

import com.example.banque.Entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    
    /**
     * Chercher un client par username
     */
    Optional<Client> findByUsername(String username);
    
    /**
     * Vérifier si un username existe
     */
    boolean existsByUsername(String username);
    
    /**
     * Vérifier si un email existe
     */
    boolean existsByEmail(String email);
}