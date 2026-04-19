package com.example.banque.controlleurs;

import com.example.banque.Entity.Client;
import com.example.banque.dto.LoginRequest;
import com.example.banque.dto.LoginResponse;
import com.example.banque.dto.RegisterRequest;
import com.example.banque.services.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Tests d'intégration du contrôleur d'authentification")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @Test
    @DisplayName("GET /api/auth/test - Endpoint public doit retourner 200")
    void testPublicEndpoint() throws Exception {
        mockMvc.perform(get("/api/auth/test"))
                .andExpect(status().isOk())
                .andExpect(content().string("Réponse publique - API fonctionnelle"));
    }

    @Test
    @DisplayName("POST /api/auth/register - Inscription réussie")
    void testRegisterSuccess() throws Exception {
        // Arrange
        RegisterRequest request = new RegisterRequest();
        request.setName("Dupont");
        request.setPrenom("Jean");
        request.setEmail("jean@example.com");
        request.setUsername("jeandupont");
        request.setPassword("Password123!");
        request.setPasswordConfirm("Password123!");
        request.setTelephone("0612345678");

        Client client = new Client();
        client.setId(1L);
        client.setName("Dupont");
        client.setPrenom("Jean");
        client.setEmail("jean@example.com");
        client.setUsername("jeandupont");
        client.setTelephone("0612345678");
        client.setActif(true);

        when(authService.register(request)).thenReturn(client);

        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("jean@example.com"))
                .andExpect(jsonPath("$.username").value("jeandupont"));
    }

    @Test
    @DisplayName("POST /api/auth/login - Connexion réussie")
    void testLoginSuccess() throws Exception {
        // Arrange
        LoginRequest request = new LoginRequest();
        request.setUsername("jeandupont");
        request.setPassword("Password123!");

        LoginResponse response = new LoginResponse();
        response.setToken("eyJhbGciOiJIUzUxMiJ9.mocktoken123");
        response.setType("Bearer");
        response.setClientId(1L);
        response.setUsername("jeandupont");
        response.setEmail("jean@example.com");

        when(authService.login(request)).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.type").value("Bearer"))
                .andExpect(jsonPath("$.clientId").value(1))
                .andExpect(jsonPath("$.username").value("jeandupont"));
    }
}
