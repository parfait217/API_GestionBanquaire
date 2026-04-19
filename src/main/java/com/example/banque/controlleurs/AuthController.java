package com.example.banque.controlleurs;

import com.example.banque.Entity.Client;
import com.example.banque.dto.LoginRequest;
import com.example.banque.dto.LoginResponse;
import com.example.banque.dto.RegisterRequest;
import com.example.banque.services.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * Contrôleur pour l'authentification (inscription et connexion)
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentification", description = "Endpoints pour s'inscrire et se connecter (publics - pas de token requis)")
public class AuthController {

    private final AuthService authService;

    /**
     * Inscription d'un nouveau client
     * POST /api/auth/register
     */
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
        summary = "S'inscrire comme nouveau client",
        description = "Créer un nouveau compte client avec email et username uniques"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Client créé avec succès",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Client.class))),
        @ApiResponse(responseCode = "400", description = "Données invalides ou username/email déjà existant")
    })
    public Client register(@Valid @RequestBody RegisterRequest request) {
        log.info("📥 POST /api/auth/register - Inscription d'un nouveau client - Username: {}", request.getUsername());
        return authService.register(request);
    }

    /**
     * Connexion d'un client
     * POST /api/auth/login
     */
    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
        summary = "Se connecter et obtenir un JWT token",
        description = "Authentifier un client et recevoir un JWT token (durée : 24h)"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Connexion réussie, JWT token retourné",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = LoginResponse.class))),
        @ApiResponse(responseCode = "401", description = "Identifiants invalides"),
        @ApiResponse(responseCode = "400", description = "Données manquantes")
    })
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        log.info("📥 POST /api/auth/login - Connexion d'un client - Username: {}", request.getUsername());
        return authService.login(request);
    }

    /**
     * Test d'authentification (endpoint public pour test)
     * GET /api/auth/test
     */
    @GetMapping("/test")
    @Operation(
        summary = "Tester l'API publiquement",
        description = "Endpoint public sans authentification pour vérifier que l'API est opérationnelle"
    )
    @ApiResponse(responseCode = "200", description = "API opérationnelle")
    public String test() {
        log.info("📥 GET /api/auth/test - Test public");
        return "Réponse publique - API fonctionnelle";
    }
}
