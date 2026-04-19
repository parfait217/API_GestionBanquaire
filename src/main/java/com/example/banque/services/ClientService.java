package com.example.banque.services;

import com.example.banque.Entity.Client;
import com.example.banque.repository.ClientRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;


    public Client creerClient(String nom, String prenom, String email, String telephone) {
        log.info("👤 Tentative de création d'un nouveau client - Nom: {} {}", nom, prenom);
        
        Client client = new Client();
        client.setName(nom);
        client.setPrenom(prenom);
        client.setEmail(email);
        client.setTelephone(telephone);
        
        Client clientCreated = clientRepository.save(client);
        log.info("✅ Client créé avec succès - ID: {} - Email: {}", clientCreated.getId(), email);
        
        return clientCreated;
    }


    public List<Client> listerTousLesClients() {
        log.debug("📋 Récupération de la liste de tous les clients");
        
        List<Client> clients = clientRepository.findAll();
        log.info("📊 {} client(s) trouvé(s)", clients.size());
        
        return clients;
    }


    public Client trouverParId(Long id) {
        log.debug("🔎 Recherche du client ID: {}", id);
        
        return clientRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("❌ Client non trouvé avec l'ID: {}", id);
                    return new EntityNotFoundException("Client pas trouve avec l'id : " + id);
                });
    }


    @Transactional
    public void supprimerClient(Long id) {
        log.info("🗑️  Tentative de suppression du client ID: {}", id);
        
        if (!clientRepository.existsById(id)) {
            log.error("❌ Client non trouvé pour la suppression - ID: {}", id);
            throw new EntityNotFoundException("Client pas trouve");
        }
        
        clientRepository.deleteById(id);
        log.info("✅ Client supprimé avec succès - ID: {}", id);
    }

    public Optional<Client> findByUsername(String username) {
        log.debug("🔎 Recherche du client par username: {}", username);
        return clientRepository.findByUsername(username);
    }
}