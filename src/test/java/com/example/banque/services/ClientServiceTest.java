package com.example.banque.services;

import com.example.banque.Entity.Client;
import com.example.banque.repository.ClientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Tests du service Client")
class ClientServiceTest {

    private ClientService clientService;

    @Mock
    private ClientRepository clientRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        clientService = new ClientService(clientRepository);
    }

    @Test
    @DisplayName("Récupérer tous les clients")
    void testGetAllClients() {
        // Arrange
        Client client1 = new Client();
        client1.setId(1L);
        client1.setName("Dupont");
        client1.setUsername("jeandupont");

        Client client2 = new Client();
        client2.setId(2L);
        client2.setName("Martin");
        client2.setUsername("martinsarah");

        List<Client> clients = Arrays.asList(client1, client2);
        when(clientRepository.findAll()).thenReturn(clients);

        // Act
        List<Client> result = clientService.listerTousLesClients();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Dupont", result.get(0).getName());
        verify(clientRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Récupérer un client par ID")
    void testGetClientById() {
        // Arrange
        Client client = new Client();
        client.setId(1L);
        client.setName("Dupont");
        client.setEmail("jean@example.com");
        client.setActif(true);

        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));

        // Act
        Client result = clientService.trouverParId(1L);

        // Assert
        assertNotNull(result);
        assertEquals("Dupont", result.getName());
        assertEquals("jean@example.com", result.getEmail());
        verify(clientRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Supprimer un client")
    void testDeleteClient() {
        // Arrange
        when(clientRepository.existsById(1L)).thenReturn(true);

        // Act
        clientService.supprimerClient(1L);

        // Assert
        verify(clientRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Chercher client par username")
    void testFindByUsername() {
        // Arrange
        Client client = new Client();
        client.setId(1L);
        client.setUsername("jeandupont");
        client.setEmail("jean@example.com");

        when(clientRepository.findByUsername("jeandupont")).thenReturn(Optional.of(client));

        // Act
        Optional<Client> result = clientService.findByUsername("jeandupont");

        // Assert
        assertTrue(result.isPresent());
        assertEquals("jeandupont", result.get().getUsername());
        verify(clientRepository, times(1)).findByUsername("jeandupont");
    }
}
