package com.example.banque.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class CompteRequest {

    @NotNull(message = "L'ID du client est obligatoire pour ouvrir un compte.")
    @Min(value = 1, message = "L'ID du client doit être supérieur à zéro.")
    @JsonProperty("client_id")
    private Long clientId;

    @NotNull(message = "Le type de compte est obligatoire.")
    @NotBlank(message = "Le type de compte ne peut être vide.")
    @Pattern(regexp = "^(COURANT|EPARGNE|TITRE)$", message = "Le type de compte doit être COURANT, EPARGNE ou TITRE")
    private String typeCompte;

    @DecimalMin(value = "0.0", inclusive = true, message = "Le solde initial ne peut pas être négatif")
    @Digits(integer = 15, fraction = 2, message = "Le solde initial ne doit pas dépasser 15 chiffres avec 2 décimales")
    private BigDecimal soldeInitial;

    public CompteRequest() {}
}