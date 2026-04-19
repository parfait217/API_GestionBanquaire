package com.example.banque.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "comptes")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString(exclude = {"clients", "transactions"})
public class Compte {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 34, unique = true, name = "numero_compte")
    private String numeroCompte;

    @Column(nullable = false, scale = 2, precision = 15)
    private BigDecimal solde = BigDecimal.ZERO;

    @Column (nullable = false, name = "date_ouvreture")
    private LocalDate dateOuverture;

    @Column(nullable = false, name = "type_compte")
    private String typeCompte;

//    Respectons la representation du diagramme UML
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    @JsonIgnore
    private Client client;

//Pour les transactions, on a :
    @OneToMany(mappedBy = "compte", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Transaction> transactions = new ArrayList<>();

    public Compte(String numeroCompte, BigDecimal soldeInitial, String typeCompte, Client client) {
        this.numeroCompte = numeroCompte;
        this.solde = soldeInitial;
        this.dateOuverture = LocalDate.now();
        this.typeCompte = typeCompte;
        this.client = client;
    }

    public void crediter(BigDecimal montant) {
        this.solde = this.solde.add(montant);
    }

    public void debiter(BigDecimal montant) {
        if (this.solde.compareTo(montant) < 0) {
            throw new IllegalArgumentException("Solde insuffisant");
        }
        this.solde = this.solde.subtract(montant);
    }
}
