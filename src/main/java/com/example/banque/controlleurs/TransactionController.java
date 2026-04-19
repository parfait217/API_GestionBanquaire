package com.example.banque.controlleurs;

import com.example.banque.Entity.Transaction;
import com.example.banque.dto.DepotRequest;
import com.example.banque.dto.RetraitRequest;
import com.example.banque.dto.VirementRequest;
import com.example.banque.services.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/operations")
@RequiredArgsConstructor
@Tag(name = "Transactions Financières", description = "Gestion des opérations bancaires (dépôts, retraits, virements - authentification requise)")
@SecurityRequirement(name = "bearer-jwt")
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/depot")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
        summary = "Effectuer un dépôt",
        description = "Augmenter le solde d'un compte en effectuant un dépôt de fonds"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Dépôt effectué avec succès"),
        @ApiResponse(responseCode = "400", description = "Montant invalide ou compte inexistant"),
        @ApiResponse(responseCode = "401", description = "Authentification requise")
    })
    public void depot(@RequestBody DepotRequest request) {
        log.info("📥 POST /api/operations/depot - Montant: {} € - Compte: {}", request.getMontant(), request.getCompteId());
        transactionService.deposer(request.getCompteId(), request.getMontant(), request.getLibelle());
    }

    @PostMapping("/retrait")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
        summary = "Effectuer un retrait",
        description = "Diminuer le solde d'un compte en retirant des fonds (vérification de solde suffisant)"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Retrait effectué avec succès"),
        @ApiResponse(responseCode = "400", description = "Solde insuffisant ou montant invalide"),
        @ApiResponse(responseCode = "401", description = "Authentification requise"),
        @ApiResponse(responseCode = "404", description = "Compte non trouvé")
    })
    public void retrait(@RequestBody RetraitRequest request) {
        log.info("📥 POST /api/operations/retrait - Montant: {} € - Compte: {}", request.getMontant(), request.getCompteId());
        transactionService.retirer(request.getCompteId(), request.getMontant(), request.getLibelle());
    }

    @PostMapping("/virement")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
        summary = "Effectuer un virement",
        description = "Transférer des fonds d'un compte source vers un compte destination (génère 2 transactions)"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Virement effectué avec succès"),
        @ApiResponse(responseCode = "400", description = "Comptes identiques, solde insuffisant ou montant invalide"),
        @ApiResponse(responseCode = "401", description = "Authentification requise"),
        @ApiResponse(responseCode = "404", description = "Compte source ou destination non trouvé")
    })
    public void virement(@RequestBody VirementRequest request) {
        log.info("📥 POST /api/operations/virement - Source: {} - Destination: {} - Montant: {} €", 
                 request.getSource(), request.getDestination(), request.getMontant());
        transactionService.virement(
                request.getSource(),
                request.getDestination(),
                request.getMontant(),
                request.getLibelle()
        );
    }

    @GetMapping("/releve/{compteId}")
    @Operation(
        summary = "Consulter l'historique des transactions",
        description = "Récupérer le relevé complet des transactions d'un compte (dépôts, retraits, virements)"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Historique retourné",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Transaction.class))),
        @ApiResponse(responseCode = "401", description = "Authentification requise"),
        @ApiResponse(responseCode = "404", description = "Compte non trouvé")
    })
    public List<Transaction> releve(@PathVariable Long compteId) {
        log.info("📥 GET /api/operations/releve/{} - Récupération de l'historique", compteId);
        return transactionService.historique(compteId);
    }
}