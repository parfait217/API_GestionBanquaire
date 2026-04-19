package com.example.banque.services;

import com.example.banque.Entity.Client;
import com.example.banque.dto.LoginRequest;
import com.example.banque.dto.LoginResponse;
import com.example.banque.dto.RegisterRequest;
import com.example.banque.exception.BanqueException;
import com.example.banque.repository.ClientRepository;
import com.example.banque.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service pour gérer l'authentification des clients
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final ClientRepository clientRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    /**
     * Inscription d'un nouveau client
     */
    @Transactional
    public Client register(RegisterRequest request) {
        log.info("📝 Tentative d'inscription - Username: {}", request.getUsername());

        // Vérifier que les mots de passe correspondent
        if (!request.getPassword().equals(request.getPasswordConfirm())) {
            log.error("❌ Les mots de passe ne correspondent pas - Username: {}", request.getUsername());
            throw new BanqueException(
                    "Les mots de passe ne correspondent pas",
                    "PASSWORD_MISMATCH",
                    "password != passwordConfirm"
            );
        }

        // Vérifier que le username n'existe pas
        if (clientRepository.existsByUsername(request.getUsername())) {
            log.error("❌ Le nom d'utilisateur existe déjà: {}", request.getUsername());
            throw new BanqueException(
                    "Le nom d'utilisateur existe déjà",
                    "USERNAME_ALREADY_EXISTS"
            );
        }

        // Vérifier que l'email n'existe pas
        if (clientRepository.existsByEmail(request.getEmail())) {
            log.error("❌ L'email existe déjà: {}", request.getEmail());
            throw new BanqueException(
                    "L'email existe déjà",
                    "EMAIL_ALREADY_EXISTS"
            );
        }

        // Créer le nouveau client
        Client client = new Client();
        client.setName(request.getName());
        client.setPrenom(request.getPrenom());
        client.setEmail(request.getEmail());
        client.setTelephone(request.getTelephone());
        client.setUsername(request.getUsername());
        client.setPassword(passwordEncoder.encode(request.getPassword()));
        client.setActif(true);

        Client savedClient = clientRepository.save(client);
        log.info("✅ Client inscrit avec succès - Username: {} - ID: {}", 
                 request.getUsername(), savedClient.getId());

        return savedClient;
    }

    /**
     * Connexion d'un client
     */
    public LoginResponse login(LoginRequest request) {
        log.info("🔑 Tentative de connexion - Username: {}", request.getUsername());

        // Chercher le client par username
        Optional<Client> clientOpt = clientRepository.findByUsername(request.getUsername());

        if (clientOpt.isEmpty()) {
            log.error("❌ Identifiants invalides - Username non trouvé: {}", request.getUsername());
            throw new BanqueException(
                    "Identifiants invalides",
                    "INVALID_CREDENTIALS"
            );
        }

        Client client = clientOpt.get();

        // Vérifier que le client est actif
        if (!client.getActif()) {
            log.error("❌ Compte désactivé - Username: {}", request.getUsername());
            throw new BanqueException(
                    "Le compte est désactivé",
                    "ACCOUNT_DISABLED"
            );
        }

        // Vérifier le mot de passe
        if (!passwordEncoder.matches(request.getPassword(), client.getPassword())) {
            log.error("❌ Mot de passe incorrect - Username: {}", request.getUsername());
            throw new BanqueException(
                    "Identifiants invalides",
                    "INVALID_CREDENTIALS"
            );
        }

        // Générer le token JWT
        String token = jwtTokenProvider.generateToken(client.getId(), client.getUsername());

        log.info("✅ Client connecté avec succès - Username: {} - ID: {}", 
                 request.getUsername(), client.getId());

        // Retourner la réponse
        return LoginResponse.builder()
                .token(token)
                .type("Bearer")
                .clientId(client.getId())
                .username(client.getUsername())
                .email(client.getEmail())
                .message("Connexion réussie")
                .build();
    }

    /**
     * Chercher un client par username
     */
    public Optional<Client> findByUsername(String username) {
        return clientRepository.findByUsername(username);
    }

    /**
     * Chercher un client par ID
     */
    public Optional<Client> findById(Long id) {
        return clientRepository.findById(id);
    }
}
