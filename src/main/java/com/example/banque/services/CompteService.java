package com.example.banque.services;

import com.example.banque.Entity.Compte;
import com.example.banque.Entity.Client;
import com.example.banque.repository.CompteRepository;
import com.example.banque.repository.ClientRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompteService {

    private final CompteRepository compteRepository;
    private final ClientRepository clientRepository;


    public Compte ouvrirCompte(Long clientId, String typeCompte, BigDecimal soldeInitial) {
        log.info("📝 Tentative d'ouverture de compte pour le client ID: {}", clientId);
        
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> {
                    log.error("❌ Client non trouvé: {}", clientId);
                    return new EntityNotFoundException("Client pas trouver");
                });

        Compte compte = new Compte();
        compte.setNumeroCompte(genererNumeroCompte());
        compte.setTypeCompte(typeCompte.toUpperCase());
        compte.setSolde(soldeInitial != null ? soldeInitial : BigDecimal.ZERO);
        compte.setDateOuverture(LocalDate.now());
        compte.setClient(client);

        Compte compteCreated = compteRepository.save(compte);
        log.info("✅ Compte créé avec succès - Numéro: {} - ID: {} - Type: {}", 
                 compteCreated.getNumeroCompte(), compteCreated.getId(), typeCompte);
        
        return compteCreated;
    }


    public List<Compte> listerComptesDuClient(Long clientId) {
        log.debug("🔍 Récupération des comptes du client ID: {}", clientId);
        
        List<Compte> comptes = compteRepository.findByClientId(clientId);
        log.info("📊 {} compte(s) trouvé(s) pour le client {}", comptes.size(), clientId);
        
        return comptes;
    }


    public BigDecimal consulterSolde(Long compteId) {
        log.debug("💰 Consultation du solde du compte ID: {}", compteId);
        
        BigDecimal solde = compteRepository.findById(compteId)
                .orElseThrow(() -> {
                    log.error("❌ Compte non trouvé: {}", compteId);
                    return new EntityNotFoundException("Compte pas trouver");
                })
                .getSolde();
        
        log.info("💹 Solde du compte {}: {} €", compteId, solde);
        return solde;
    }

    // Trouver un compte avec vérification
    public Compte trouverCompteParId(Long compteId) {
        log.debug("🔎 Recherche du compte ID: {}", compteId);
        
        return compteRepository.findById(compteId)
                .orElseThrow(() -> {
                    log.error("❌ Compte non trouvé: {}", compteId);
                    return new EntityNotFoundException("Compte pas trouver : " + compteId);
                });
    }

    private String genererNumeroCompte() {
        String numeroCompte = "FR76" + System.nanoTime();
        log.debug("🎫 Numéro de compte généré: {}", numeroCompte);
        return numeroCompte;
    }
}