package com.example.banque.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Service pour gérer les JWT (JSON Web Tokens)
 */
@Slf4j
@Service
public class JwtTokenProvider {

    @Value("${app.jwtSecret:banque_secret_key_super_securisee_2026_gestion_bancaire}")
    private String jwtSecret;

    @Value("${app.jwtExpirationMs:86400000}") // 24 heures par défaut
    private long jwtExpirationMs;

    /**
     * Générer un JWT token
     */
    public String generateToken(Long userId, String username) {
        log.info("🔐 Génération d'un JWT token pour l'utilisateur: {}", username);
        
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        
        String token = createToken(claims, username);
        log.debug("✅ Token généré avec succès pour: {}", username);
        
        return token;
    }

    /**
     * Créer un token JWT
     */
    private String createToken(Map<String, Object> claims, String subject) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(key)
                .compact();
    }

    /**
     * Extraire le nom d'utilisateur du token
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extraire l'ID utilisateur du token
     */
    public Long extractUserId(String token) {
        Claims claims = extractAllClaims(token);
        Object userId = claims.get("userId");
        
        if (userId instanceof Integer) {
            return ((Integer) userId).longValue();
        }
        return (Long) userId;
    }

    /**
     * Extraire une claim du token
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extraire tous les claims du token
     */
    private Claims extractAllClaims(String token) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Valider un token JWT
     */
    public boolean validateToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
            
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
            
            log.debug("✅ Token valide");
            return true;
        } catch (Exception e) {
            log.error("❌ Erreur de validation du token: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Vérifier si le token est expiré
     */
    public boolean isTokenExpired(String token) {
        try {
            Date expiration = extractClaim(token, Claims::getExpiration);
            return expiration.before(new Date());
        } catch (Exception e) {
            log.error("❌ Erreur lors de la vérification de l'expiration: {}", e.getMessage());
            return true;
        }
    }
}
