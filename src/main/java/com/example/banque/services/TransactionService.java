package com.example.banque.services;

import com.example.banque.Entity.Compte;
import com.example.banque.Entity.Transaction;
import com.example.banque.repository.CompteRepository;
import com.example.banque.repository.TransactionRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionService {

    private final CompteRepository compteRepository;
    private final TransactionRepository transactionRepository;

    @Transactional
    public void deposer(Long compteId, BigDecimal montant, String libelle) {
        log.info("💷 Dépôt en cours - Compte: {} - Montant: {} € - Libellé: {}", compteId, montant, libelle);
        
        valideMontant(montant);
        Compte compte = getCompte(compteId);

        BigDecimal ancienSolde = compte.getSolde();
        compte.crediter(montant);
        creerTransaction(compte, montant, "CREDIT", libelle + " (dépôt)");
        
        log.info("✅ Dépôt effectué - Compte: {} - Ancien solde: {} € - Nouveau solde: {} €", 
                 compteId, ancienSolde, compte.getSolde());
    }

    @Transactional
    public void retirer(Long compteId, BigDecimal montant, String libelle) {
        log.info("💸 Retrait en cours - Compte: {} - Montant: {} € - Libellé: {}", compteId, montant, libelle);
        
        valideMontant(montant);
        Compte compte = getCompte(compteId);

        if (compte.getSolde().compareTo(montant) < 0) {
            log.error("❌ Solde insuffisant - Compte: {} - Solde: {} € - Montant demandé: {} €", 
                     compteId, compte.getSolde(), montant);
            throw new IllegalArgumentException("Solde insuffisant");
        }

        BigDecimal ancienSolde = compte.getSolde();
        compte.debiter(montant);
        creerTransaction(compte, montant, "DEBIT", libelle + " (retrait)");
        
        log.info("✅ Retrait effectué - Compte: {} - Ancien solde: {} € - Nouveau solde: {} €", 
                 compteId, ancienSolde, compte.getSolde());
    }

    @Transactional
    public void virement(Long sourceId, Long destinationId, BigDecimal montant, String libelle) {
        log.info("🔄 Virement en cours - Source: {} - Destination: {} - Montant: {} € - Libellé: {}", 
                 sourceId, destinationId, montant, libelle);
        
        if (sourceId.equals(destinationId)) {
            log.error("❌ Virement impossible - Source et destination identiques: {}", sourceId);
            throw new IllegalArgumentException("Impossible de virer sur le même compte");
        }

        valideMontant(montant);

        Compte source = getCompte(sourceId);
        Compte destination = getCompte(destinationId);

        if (source.getSolde().compareTo(montant) < 0) {
            log.error("❌ Solde insuffisant pour le virement - Compte source: {} - Solde: {} € - Montant: {} €", 
                     sourceId, source.getSolde(), montant);
            throw new IllegalArgumentException("Solde insuffisant");
        }

        BigDecimal ancienSoldeSource = source.getSolde();
        BigDecimal ancienSoldeDestination = destination.getSolde();
        
        source.debiter(montant);
        destination.crediter(montant);

        creerTransaction(source, montant, "VIREMENT_EMIS", libelle + " vers compte " + destination.getNumeroCompte());
        creerTransaction(destination, montant, "VIREMENT_RECU", libelle + " depuis compte " + source.getNumeroCompte());
        
        log.info("✅ Virement effectué - Source: {} ({} → {} €) - Destination: {} ({} → {} €)", 
                 sourceId, ancienSoldeSource, source.getSolde(),
                 destinationId, ancienSoldeDestination, destination.getSolde());
    }

    public List<Transaction> historique(Long compteId) {
        log.debug("📜 Récupération de l'historique du compte: {}", compteId);
        
        if (!compteRepository.existsById(compteId)) {
            log.error("❌ Compte non trouvé pour l'historique: {}", compteId);
            throw new EntityNotFoundException("Compte non trouvé : " + compteId);
        }
        
        List<Transaction> transactions = transactionRepository.findByCompteIdOrderByDateTransactionDesc(compteId);
        log.info("📊 {} transaction(s) trouvée(s) pour le compte {}", transactions.size(), compteId);
        
        return transactions;
    }


    private Compte getCompte(Long compteId) {
        return compteRepository.findById(compteId)
                .orElseThrow(() -> {
                    log.error("❌ Compte non trouvé: {}", compteId);
                    return new EntityNotFoundException("Compte non trouvé : " + compteId);
                });
    }

    private void creerTransaction(Compte compte, BigDecimal montant, String type, String description) {
        Transaction tx = new Transaction();
        tx.setMontant(montant);
        tx.setTypeTransaction(type);
        tx.setDescription(description);
        tx.setDateTransaction(LocalDateTime.now());
        tx.setCompte(compte);
        transactionRepository.save(tx);
        log.debug("📋 Transaction créée - Type: {} - Montant: {} € - Compte: {}", type, montant, compte.getId());
    }

    private void valideMontant(BigDecimal montant) {
        if (montant == null || montant.compareTo(BigDecimal.ZERO) <= 0) {
            log.error("❌ Montant invalide: {}", montant);
            throw new IllegalArgumentException("Le montant doit être positif");
        }
    }
}