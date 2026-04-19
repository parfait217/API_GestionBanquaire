package com.example.banque.services;

import com.example.banque.Entity.Client;
import com.example.banque.dto.LoginRequest;
import com.example.banque.dto.LoginResponse;
import com.example.banque.dto.RegisterRequest;
import com.example.banque.repository.ClientRepository;
import com.example.banque.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Tests du service d'authentification")
class AuthServiceTest {

    private AuthService authService;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    private BCryptPasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        passwordEncoder = new BCryptPasswordEncoder();
        authService = new AuthService(clientRepository, jwtTokenProvider, passwordEncoder);
    }

    @Test
    @DisplayName("Inscription réussie d'un nouveau client")
    void testRegisterSuccess() {
        // Arrange
        RegisterRequest request = new RegisterRequest();
        request.setName("Dupont");
        request.setPrenom("Jean");
        request.setEmail("jean@example.com");
        request.setUsername("jeandupont");
        request.setPassword("Password123!");
        request.setPasswordConfirm("Password123!");
        request.setTelephone("0612345678");

        when(clientRepository.existsByUsername("jeandupont")).thenReturn(false);
        when(clientRepository.existsByEmail("jean@example.com")).thenReturn(false);
        when(clientRepository.save(any(Client.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Client result = authService.register(request);

        // Assert
        assertNotNull(result);
        assertEquals("Dupont", result.getName());
        assertEquals("jean@example.com", result.getEmail());
        assertTrue(result.getActif());
        verify(clientRepository, times(1)).save(any(Client.class));
    }

    @Test
    @DisplayName("Inscription échouée si username existe")
    void testRegisterFailureUsernameExists() {
        // Arrange
        RegisterRequest request = new RegisterRequest();
        request.setUsername("jeandupont");
        request.setEmail("jean@example.com");
        request.setPassword("Password123!");
        request.setPasswordConfirm("Password123!");

        when(clientRepository.existsByUsername("jeandupont")).thenReturn(true);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> authService.register(request));
    }

    @Test
    @DisplayName("Connexion réussie avec JWT")
    void testLoginSuccess() {
        // Arrange
        LoginRequest request = new LoginRequest();
        request.setUsername("jeandupont");
        request.setPassword("Password123!");

        Client client = new Client();
        client.setId(1L);
        client.setUsername("jeandupont");
        client.setEmail("jean@example.com");
        client.setPassword(passwordEncoder.encode("Password123!"));
        client.setActif(true);

        String mockToken = "eyJhbGciOiJIUzUxMiJ9.mocktoken123";

        when(clientRepository.findByUsername("jeandupont")).thenReturn(java.util.Optional.of(client));
        when(jwtTokenProvider.generateToken(1L, "jeandupont")).thenReturn(mockToken);

        // Act
        LoginResponse result = authService.login(request);

        // Assert
        assertNotNull(result);
        assertEquals(mockToken, result.getToken());
        assertEquals("jeandupont", result.getUsername());
        assertEquals("Bearer", result.getType());
        verify(clientRepository, times(1)).findByUsername("jeandupont");
    }
}
