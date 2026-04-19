package com.example.banque.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "comptes")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal montant;

    @Column(name = "date_transaction", nullable = false)
    private LocalDateTime dateTransaction = LocalDateTime.now();

    @Column(name = "type_transaction", nullable = false, length = 20)
    private String typeTransaction;

    @Column(length = 255)
    private String description;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "compte_id", nullable = false)
    @JsonIgnore
    private Compte compte;

    // Constructeur lie a une transaction
    public Transaction(BigDecimal montant, String typeTransaction, String description, Compte compte) {
        this.montant = montant;
        this.typeTransaction = typeTransaction;
        this.description = description;
        this.compte = compte;
        this.dateTransaction = LocalDateTime.now();
    }
}