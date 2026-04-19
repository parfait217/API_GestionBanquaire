package com.example.banque.controlleurs;



import com.example.banque.Entity.Compte;
import com.example.banque.dto.CompteRequest;
import com.example.banque.services.CompteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/comptes")
@RequiredArgsConstructor
@Tag(name = "Comptes Bancaires", description = "Gestion des comptes bancaires (authentification requise)")
@SecurityRequirement(name = "bearer-jwt")
public class CompteController {

    private final CompteService compteService;

    @PostMapping
    @Operation(
        summary = "Ouvrir un nouveau compte",
        description = "Créer un compte bancaire pour un client existant (COURANT, EPARGNE ou TITRE)"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Compte créé avec succès",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Compte.class))),
        @ApiResponse(responseCode = "400", description = "Données invalides ou type compte incorrect"),
        @ApiResponse(responseCode = "401", description = "Authentification requise"),
        @ApiResponse(responseCode = "404", description = "Client non trouvé")
    })
    public Compte ouvrir(@Valid @RequestBody CompteRequest request) {
        log.info("📥 POST /api/comptes - Ouverture d'un compte pour le client {}", request.getClientId());
        return compteService.ouvrirCompte(
                request.getClientId(),
                request.getTypeCompte(),
                request.getSoldeInitial()
        );
    }

    @GetMapping("/client/{client_id}")
    @Operation(
        summary = "Lister les comptes d'un client",
        description = "Récupérer tous les comptes bancaires d'un client spécifique"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Liste des comptes retournée",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Compte.class))),
        @ApiResponse(responseCode = "401", description = "Authentification requise"),
        @ApiResponse(responseCode = "404", description = "Client non trouvé")
    })
    public List<Compte> duClient(@PathVariable Long client_id) {
        log.info("📥 GET /api/comptes/client/{} - Récupération des comptes du client", client_id);
        return compteService.listerComptesDuClient(client_id);
    }

    @GetMapping("/{compteId}/solde")
    @Operation(
        summary = "Consulter le solde d'un compte",
        description = "Obtenir le solde disponible sur un compte spécifique"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Solde retourné (BigDecimal)"),
        @ApiResponse(responseCode = "401", description = "Authentification requise"),
        @ApiResponse(responseCode = "404", description = "Compte non trouvé")
    })
    public BigDecimal solde(@PathVariable Long compteId) {
        log.info("📥 GET /api/comptes/{}/solde - Consultation du solde", compteId);
        return compteService.consulterSolde(compteId);
    }
}