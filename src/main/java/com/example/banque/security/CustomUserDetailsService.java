package com.example.banque.security;

import com.example.banque.Entity.Client;
import com.example.banque.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Service pour charger les détails de l'utilisateur depuis la base de données
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final ClientRepository clientRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("🔍 Chargement des détails de l'utilisateur: {}", username);

        Optional<Client> clientOpt = clientRepository.findByUsername(username);
        Client client = clientOpt.orElseThrow(() -> {
            log.error("❌ Utilisateur non trouvé: {}", username);
            return new UsernameNotFoundException("Utilisateur non trouvé: " + username);
        });

        List<GrantedAuthority> authorities = new ArrayList<>();

        log.debug("✅ Utilisateur trouvé et chargé: {}", username);

        return User.builder()
                .username(client.getUsername())
                .password(client.getPassword())
                .authorities(authorities)
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(!client.getActif())
                .build();
    }
}
