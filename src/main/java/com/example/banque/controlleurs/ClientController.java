package com.example.banque.controlleurs;


import com.example.banque.Entity.Client;
import com.example.banque.services.ClientService;
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

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
@Tag(name = "Gestion des Clients", description = "Gestion des clients bancaires (authentification requise)")
@SecurityRequirement(name = "bearer-jwt")
public class ClientController {

    private final ClientService clientService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
        summary = "Créer un nouveau client",
        description = "Enregistrer un nouveau client bancaire dans le système"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Client créé avec succès",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Client.class))),
        @ApiResponse(responseCode = "400", description = "Données invalides"),
        @ApiResponse(responseCode = "401", description = "Authentification requise")
    })
    public Client creer(@RequestBody Client client) {
        log.info("📥 POST /api/clients - Création d'un client");
        return clientService.creerClient(
                client.getName(),
                client.getPrenom(),
                client.getEmail(),
                client.getTelephone()
        );
    }

    @GetMapping
    @Operation(
        summary = "Lister tous les clients",
        description = "Récupérer la liste complète de tous les clients bancaires"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Liste des clients retournée",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Client.class))),
        @ApiResponse(responseCode = "401", description = "Authentification requise")
    })
    public List<Client> lister() {
        log.info("📥 GET /api/clients - Récupération de la liste des clients");
        return clientService.listerTousLesClients();
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Consulter un client",
        description = "Récupérer les détails d'un client spécifique par ID"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Client trouvé",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Client.class))),
        @ApiResponse(responseCode = "401", description = "Authentification requise"),
        @ApiResponse(responseCode = "404", description = "Client non trouvé")
    })
    public Client trouver(@PathVariable Long id) {
        log.info("📥 GET /api/clients/{} - Récupération d'un client", id);
        return clientService.trouverParId(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
        summary = "Supprimer un client",
        description = "Supprimer un client et ses comptes associés du système"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Client supprimé avec succès"),
        @ApiResponse(responseCode = "401", description = "Authentification requise"),
        @ApiResponse(responseCode = "404", description = "Client non trouvé")
    })
    public void supprimer(@PathVariable Long id) {
        log.info("📥 DELETE /api/clients/{} - Suppression d'un client", id);
        clientService.supprimerClient(id);
    }
}