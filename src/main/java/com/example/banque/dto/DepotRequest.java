package com.example.banque.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepotRequest {
    
    @NotNull(message = "L'ID du compte est obligatoire")
    @Min(value = 1, message = "L'ID du compte doit être supérieur à zéro")
    private Long compteId;
    
    @NotNull(message = "Le montant est obligatoire")
    @DecimalMin(value = "0.01", message = "Le montant doit être supérieur à 0.01")
    @Digits(integer = 15, fraction = 2, message = "Le montant ne doit pas dépasser 15 chiffres avec 2 décimales")
    private BigDecimal montant;
    
    @NotBlank(message = "Le libellé est obligatoire")
    @Size(min = 3, max = 255, message = "Le libellé doit contenir entre 3 et 255 caractères")
    private String libelle;
}