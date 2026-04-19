# 📋 CAHIER DES CHARGES - Devoir 304
## Système de Gestion Bancaire - API REST Sécurisée

---

## 1️⃣ CONTEXTE ET OBJECTIFS

### Contexte
Développer une application de gestion bancaire permettant aux clients de s'inscrire, se connecter et gérer leurs comptes avec des opérations financières sécurisées (dépôts, retraits, virements).

### Objectifs
- ✅ Gérer l'authentification des clients (Inscription/Connexion)
- ✅ Gérer les clients bancaires
- ✅ Créer et consulter les comptes
- ✅ Effectuer des transactions (dépôts, retraits)
- ✅ Maintenir un historique des opérations
- ✅ Assurer la cohérence des données
- ✅ Fournir une API REST sécurisée avec JWT
- ✅ Implémenter une gestion d'erreurs robuste

---

## 2️⃣ ACTEURS

| Acteur | Description |
|--------|-------------|
| **Client** | Personne physique utilisant l'application pour gérer ses comptes |
| **Système d'Authentification** | Gère l'inscription, la connexion et la validation JWT |
| **Système** | Traite les transactions et maintient l'intégrité des données |

---

## 3️⃣ SPÉCIFICATIONS FONCTIONNELLES

### 3.0 Gestion de l'Authentification (🆕 NOUVEAU)
| Fonction | Description | Précisions |
|----------|-------------|-----------|
| **S'inscrire** | Créer un nouveau compte client | Username unique, email unique, mot de passe hashé |
| **Se connecter** | Obtenir un JWT token | Authentification username/password |
| **Utiliser les tokens** | Accéder aux endpoints protégés | Bearer token dans header Authorization |
| **Valider les tokens** | Vérifier la validité du JWT | Expiration 24h |

### 3.1 Gestion des Clients
| Fonction | Description | Précisions |
|----------|-------------|-----------|
| **Lister les clients** | Obtenir la liste de tous les clients | Endpoints protégés - token requis |
| **Consulter un client** | Afficher les détails d'un client | Par ID client |
| **Supprimer un client** | Retirer un client du système | Cascade : supprime les comptes et transactions associés |

### 3.2 Gestion des Comptes
| Fonction | Description | Précisions |
|----------|-------------|-----------|
| **Ouvrir un compte** | Créer un nouveau compte pour un client | Client existe, type déterminé (COURANT/EPARGNE), solde initial optionnel |
| **Lister les comptes** | Obtenir les comptes d'un client | Filtre par `client_id` - token requis |
| **Consulter un compte** | Voir les détails d'un compte | Numéro de compte, solde, type, date ouverture |
| **Consulter le solde** | Vérifier le solde d'un compte | En temps réel |

### 3.3 Gestion des Transactions
| Fonction | Description | Précisions |
|----------|-------------|-----------|
| **Effectuer un dépôt** | Ajouter de l'argent sur un compte | Type : CREDIT, vérifie montant > 0 |
| **Effectuer un retrait** | Retirer de l'argent d'un compte | Type : DEBIT, vérifie solde suffisant |
| **Effectuer un virement** | Transférer argent entre deux comptes | Génère 2 transactions (EMIS/RECU) |
| **Consulter l'historique** | Voir toutes les transactions d'un compte | Trié par date (plus récent en premier) |

---

## 4️⃣ SPÉCIFICATIONS NON-FONCTIONNELLES

### Performance
- ⚡ **Temps de réponse** : < 500ms pour 95% des requêtes
- 📊 **Débit** : Supporter min. 1000 opérations/minute
- 💾 **Scalabilité** : Architecture extensible et modulaire

### Sécurité (🆕 AMÉLIORÉ)
- 🔐 **Authentification** : JWT HS512 avec expiration 24h
- 🔒 **Validation** : Toutes les données d'entrée validées côté serveur
- 🔑 **Hashage Mots de Passe** : BCrypt
- 🛡️ **Intégrité** : Transactions ACID garanties
- 📝 **Audit** : Historique de chaque opération conservé
- 🚫 **Endpoints Protégés** : Authentification requise
- 📡 **HTTPS Ready** : Configuration pour HTTPS en production

### Fiabilité
- ✅ **Cohérence des données** : Solde toujours correct
- 🔄 **Atomicité** : Opération = tout ou rien
- 🚨 **Gestion d'erreurs** : Messages d'erreur clairs et appropriés
- 📋 **Logging** : Traçabilité complète des opérations

### Accessibilité
- 📡 **API REST** : Endpoints standardisés
- 📋 **Documentation** : Endpoints bien documentés
- 🔌 **Format** : JSON pour requêtes et réponses

---

## 5️⃣ CAS D'UTILISATION

### CU0 : S'inscrire (🆕 NOUVEAU)
**Acteur** : Nouveau Client  
**Préconditions** : Aucune  
**Étapes** :
1. Fournir : nom, prénom, email, téléphone, username, password
2. Système valide l'unicité de username et email
3. Système vérifie que les mots de passe correspondent
4. Client créé avec mot de passe hashé
5. Retour confirmation avec détails du compte

### CU0b : Se connecter (🆕 NOUVEAU)
**Acteur** : Client  
**Préconditions** : Client inscrit  
**Étapes** :
1. Fournir username et password
2. Système valide les identifiants
3. JWT token généré
4. Token retourné avec clientId, email, username
5. Client peut utiliser le token dans les requêtes suivantes

### CU1 : Ouvrir un compte
**Acteur** : Client (authentifié)  
**Préconditions** : Client connecté (JWT token valide)  
**Étapes** :
1. Fournir l'ID client, type de compte et solde initial
2. Système génère un numéro de compte unique
3. Compte créé avec solde initialisé
4. Retour confirmation avec détails du compte

### CU2 : Effectuer un dépôt
**Acteur** : Client (authentifié)  
**Préconditions** : Compte existant, JWT token valide  
**Étapes** :
1. Fournir ID compte, montant, libellé
2. Système valide montant > 0
3. Solde augmenté, transaction créée (CREDIT)
4. Retour confirmation

### CU3 : Effectuer un retrait
**Acteur** : Client (authentifié)  
**Préconditions** : Compte existant, solde suffisant, JWT token valide  
**Étapes** :
1. Fournir ID compte, montant, libellé
2. Système valide montant > 0 et solde suffisant
3. Solde diminué, transaction créée (DEBIT)
4. Retour confirmation (ou erreur si solde insuffisant)

### CU4 : Effectuer un virement
**Acteur** : Client (authentifié)  
**Préconditions** : Deux comptes existants, solde suffisant, JWT token valide  
**Étapes** :
1. Fournir ID compte source, destination, montant, libellé
2. Système valide comptes ≠ et solde source suffisant
3. Débit compte source, crédit compte destination
4. 2 transactions générées (une par compte)
5. Retour confirmation

### CU5 : Consulter l'historique
**Acteur** : Client (authentifié)  
**Préconditions** : Compte existant, JWT token valide  
**Étapes** :
1. Fournir ID compte
2. Système retourne liste triée (récent → ancien)
3. Montrant type, montant, date, description

---

## 6️⃣ API REST - Endpoints

### 🔐 Authentification (Publics / Sans token)
```
POST   /api/auth/register                → S'inscrire
POST   /api/auth/login                   → Se connecter
GET    /api/auth/test                    → Test public (vérification API)
```

### 🧑 Clients (Protégés / Token requis)
```
GET    /api/clients                      → Lister tous les clients
GET    /api/clients/{id}                 → Consulter un client
DELETE /api/clients/{id}                 → Supprimer un client
```

### 🏦 Comptes (Protégés / Token requis)
```
POST   /api/comptes                      → Ouvrir un compte
GET    /api/comptes/client/{client_id}   → Lister comptes d'un client
GET    /api/comptes/{compteId}/solde     → Consulter solde
```

### 💳 Transactions (Protégés / Token requis)
```
POST   /api/operations/depot             → Effectuer un dépôt
POST   /api/operations/retrait           → Effectuer un retrait
POST   /api/operations/virement          → Effectuer un virement
GET    /api/operations/releve/{compteId} → Historique des opérations
```

---

## 7️⃣ MODÈLE DE DONNÉES

### Client (Modifié 🆕)
```java
- id (Long)
- name (String)
- prenom (String)
- email (String) - unique
- username (String) - unique
- password (String) - hashé avec BCrypt
- telephone (String)
- actif (Boolean) - pour désactiver les comptes
- comptes (List<Compte>)
```

### Compte
```java
- id (Long)
- numeroCompte (String) - unique, IBAN
- solde (BigDecimal)
- dateOuverture (LocalDate)
- typeCompte (String) - COURANT, EPARGNE, TITRE
- client (Client)
- transactions (List<Transaction>)
```

### Transaction
```java
- id (Long)
- montant (BigDecimal)
- dateTransaction (LocalDateTime)
- typeTransaction (String) - CREDIT, DEBIT, VIREMENT_EMIS, VIREMENT_RECU
- description (String)
- compte (Compte)
```

---

## 8️⃣ EXEMPLES DE REQUÊTES/RÉPONSES

### 🔐 AUTHENTIFICATION

#### 1️⃣ POST /api/auth/register - S'inscrire
**Request** :
```json
{
  "name": "Dupont",
  "prenom": "Jean",
  "email": "jean.dupont@example.com",
  "telephone": "0612345678",
  "username": "jeandupont",
  "password": "SecurePassword123!",
  "passwordConfirm": "SecurePassword123!"
}
```
**Response** (201 Created) :
```json
{
  "id": 1,
  "name": "Dupont",
  "prenom": "Jean",
  "email": "jean.dupont@example.com",
  "username": "jeandupont",
  "telephone": "0612345678",
  "actif": true
}
```

#### 2️⃣ POST /api/auth/login - Se connecter
**Request** :
```json
{
  "username": "jeandupont",
  "password": "SecurePassword123!"
}
```
**Response** (200 OK) :
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJqZWFuZHVwb250IiwiY2xpZW50SWQiOjEsImlhdCI6MTcxMjk0NTMyMCwiZXhwIjoxNzEzMDMxNzIwfQ.f5K2j8n...",
  "type": "Bearer",
  "clientId": 1,
  "username": "jeandupont",
  "email": "jean.dupont@example.com",
  "message": "Connexion réussie"
}
```

### 🏦 CLIENTS

#### 3️⃣ GET /api/clients - Lister tous les clients
**Header** :
```
Authorization: Bearer <token_reçu>
```
**Response** (200 OK) :
```json
[
  {
    "id": 1,
    "name": "Dupont",
    "prenom": "Jean",
    "email": "jean.dupont@example.com",
    "username": "jeandupont",
    "telephone": "0612345678",
    "actif": true
  }
]
```

### 💳 COMPTES

#### 4️⃣ POST /api/comptes - Ouvrir un compte
**Header** :
```
Authorization: Bearer <token>
Content-Type: application/json
```
**Request** :
```json
{
  "client_id": 1,
  "typeCompte": "COURANT",
  "soldeInitial": 1000.00
}
```
**Response** (201 Created) :
```json
{
  "id": 101,
  "numeroCompte": "FR7612345678901234567890123",
  "solde": 1000.00,
  "dateOuverture": "2026-04-12",
  "typeCompte": "COURANT"
}
```

#### 5️⃣ POST /api/operations/depot - Effectuer un dépôt
**Header** :
```
Authorization: Bearer <token>
Content-Type: application/json
```
**Request** :
```json
{
  "compteId": 101,
  "montant": 500.00,
  "libelle": "Salaire mensuel"
}
```
**Response** (201 Created) :
```json
{
  "success": true,
  "message": "Opération réussie",
  "data": null
}
```

#### 6️⃣ GET /api/operations/releve/101 - Historique
**Header** :
```
Authorization: Bearer <token>
```
**Response** (200 OK) :
```json
[
  {
    "id": 1,
    "montant": 500.00,
    "dateTransaction": "2026-04-12T10:30:00",
    "typeTransaction": "CREDIT",
    "description": "Salaire mensuel (dépôt)"
  },
  {
    "id": 2,
    "montant": 100.00,
    "dateTransaction": "2026-04-11T14:20:00",
    "typeTransaction": "DEBIT",
    "description": "Retrait espèces (retrait)"
  }
]
```

---

## 9️⃣ GESTION DES ERREURS

### Codes et Formats
| Code | Erreur | Exemple |
|------|--------|---------|
| 400 | Bad Request | Montant invalide ou négatif |
| 401 | Unauthorized | Token manquant ou invalide |
| 403 | Forbidden | Accès refusé |
| 404 | Not Found | Client/Compte/Transaction inexistant |
| 409 | Conflict | Username ou email déjà existant |
| 500 | Server Error | Erreur base de données |

### Format d'erreur standardisé
```json
{
  "success": false,
  "message": "Montant invalide",
  "errorCode": "INVALID_ARGUMENT",
  "timestamp": "2026-04-12T10:30:00",
  "path": "/api/operations/depot"
}
```

### Erreurs d'Authentification
```json
{
  "success": false,
  "status": 401,
  "message": "Token d'authentification manquant ou invalide",
  "error": "Unauthorized",
  "timestamp": 1712945320000,
  "path": "/api/clients"
}
```

---

## 🔟 TECHNOLOGIES

| Composant | Technologie |
|-----------|-------------|
| **Backend** | Spring Boot 3.5.7 |
| **ORM** | JPA/Hibernate |
| **Authentification** | Spring Security + JWT (JJWT) |
| **Base de données** | MySQL |
| **Validation** | Jakarta Validation |
| **Logging** | SLF4J + Logback |
| **Java** | JDK 17 |
| **Build** | Maven 3.8+ |
| **Hashage** | BCrypt |

---

## 1️⃣1️⃣ FLUX D'AUTHENTIFICATION

```
┌─────────────────────────────────────────────────────────┐
│           FLUX D'AUTHENTIFICATION JWT                   │
├─────────────────────────────────────────────────────────┤
│                                                         │
│ 1️⃣  INSCRIPTION                                        │
│    └─ POST /api/auth/register                          │
│       └─ Valider données + Créer compte                │
│          └─ ✅ Client créé (password hashé)            │
│                                                         │
│ 2️⃣  CONNEXION                                          │
│    └─ POST /api/auth/login                             │
│       └─ Vérifier identifiants                         │
│          └─ 🔐 Générer JWT Token                       │
│             └─ ✅ Retourner Token (24h)                │
│                                                         │
│ 3️⃣  REQUÊTES PROTÉGÉES                                │
│    └─ GET /api/clients + Bearer Token                  │
│       └─ Filtre JWT valide le token                    │
│          ├─ ✅ Token valide → Accès autorisé           │
│          └─ ❌ Token invalide → 401 Unauthorized       │
│                                                         │
└─────────────────────────────────────────────────────────┘
```

---

## 1️⃣2️⃣ DÉPLOIEMENT ET TESTS

### Exécution
- ✅ Application standalone JAR
- ✅ Configuration MySQL automatique
- ✅ Migrations Hibernate auto (ddl-auto=update)

### Tests
- ✅ Unit Tests avec JUnit
- ✅ Tests d'intégration
- ✅ Postman collection fournie (voir JWT_AUTHENTICATION_GUIDE.md)

### Endpoints de Test
```bash
# Test public (pas de token requis)
curl http://localhost:8080/api/auth/test

# Inscription
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{...}'

# Connexion
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"user","password":"pass"}'

# Requête protégée
curl http://localhost:8080/api/clients \
  -H "Authorization: Bearer <token>"
```

---

## 1️⃣3️⃣ AMÉLIORATIONS IMPLÉMENTÉES

### Code Quality 🎯
- ✅ Logging structuré avec emojis (INFO, DEBUG, ERROR)
- ✅ Gestion d'erreurs globale (GlobalExceptionHandler)
- ✅ Exceptions personnalisées (BanqueException)
- ✅ DTOs avec validations complètes
- ✅ Architecture épurée (Services/Contrôleurs/Repositories)

### Sécurité 🔐
- ✅ Authentification JWT HS512
- ✅ Hashage BCrypt des mots de passe
- ✅ Validation des données d'entrée
- ✅ Endpoints protégés par défaut
- ✅ Gestion des erreurs sans révéler d'infos sensibles

### Performance ⚡
- ✅ Requêtes optimisées
- ✅ Stateless (pas de session serveur)
- ✅ Transactions ACID
- ✅ Logging en niveau approprié

---

## 📚 DOCUMENTATION

- **[CAHIER_CHARGES.md](CAHIER_CHARGES.md)** - Ce document
- **[JWT_AUTHENTICATION_GUIDE.md](JWT_AUTHENTICATION_GUIDE.md)** - Guide d'authentification complet

---

## 📊 RÉCAPITULATIF DU PROJET

| Aspect | Détails |
|--------|---------|
| **Endpoints** | 14 endpoints (3 publics, 11 protégés) |
| **DTOs** | 7 DTOs avec validations |
| **Services** | 5 services métier |
| **Repositories** | 3 repositories JPA |
| **Entités** | 3 entités (Client, Compte, Transaction) |
| **Sécurité** | JWT + Spring Security + BCrypt |
| **Logging** | SLF4J + Logback |
| **Gestion Erreurs** | Globale + Exceptions personnalisées |
| **Transactions** | ACID avec @Transactional |

---

**Date** : 12 Avril 2026  
**Cours** : ICT304 - Gestion Bancaire  
**Version** : 2.0 - Avec Authentification JWT  
**État** : ✅ **Production Ready**

---

## 2️⃣ ACTEURS

| Acteur | Description |
|--------|-------------|
| **Client** | Personne physique utilisant l'application pour gérer ses comptes |
| **Administrateur** | Gère les opérations critiques et supervise le système |
| **Système** | Traite les transactions et maintient l'intégrité des données |

---

## 3️⃣ SPÉCIFICATIONS FONCTIONNELLES

### 3.1 Gestion des Clients
| Fonction | Description | Précisions |
|----------|-------------|-----------|
| **Créer un client** | Ajouter un nouveau client dans le système | Informations : nom, prénom, email, téléphone |
| **Lister les clients** | Obtenir la liste de tous les clients | Retourne tous les clients enregistrés |
| **Consulter un client** | Afficher les détails d'un client | Par ID client |
| **Supprimer un client** | Retirer un client du système | Cascade : supprime les comptes et transactions associés |

### 3.2 Gestion des Comptes
| Fonction | Description | Précisions |
|----------|-------------|-----------|
| **Ouvrir un compte** | Créer un nouveau compte pour un client | Client existe, type déterminé (COURANT/EPARGNE), solde initial optionnel |
| **Lister les comptes** | Obtenir les comptes d'un client | Filtre par `client_id` |
| **Consulter un compte** | Voir les détails d'un compte | Numéro de compte, solde, type, date ouverture |
| **Consulter le solde** | Vérifier le solde d'un compte | En temps réel |

### 3.3 Gestion des Transactions
| Fonction | Description | Précisions |
|----------|-------------|-----------|
| **Effectuer un dépôt** | Ajouter de l'argent sur un compte | Type : CREDIT, vérifie montant > 0 |
| **Effectuer un retrait** | Retirer de l'argent d'un compte | Type : DEBIT, vérifie solde suffisant |
| **Effectuer un virement** | Transférer argent entre deux comptes | Génère 2 transactions (EMIS/RECU) |
| **Consulter l'historique** | Voir toutes les transactions d'un compte | Trié par date (plus récent en premier) |

---

## 4️⃣ SPÉCIFICATIONS NON-FONCTIONNELLES

### Performance
- ⚡ **Temps de réponse** : < 500ms pour 95% des requêtes
- 📊 **Débit** : Supporter min. 1000 opérations/minute
- 💾 **Scalabilité** : Architecture extensible et modulaire

### Sécurité
- 🔒 **Validation** : Toutes les données d'entrée validées côté serveur
- 🛡️ **Intégrité** : Transactions ACID garanties
- 📝 **Audit** : Historique de chaque opération conservé

### Fiabilité
- ✅ **Cohérence des données** : Solde toujours correct
- 🔄 **Atomicité** : Opération = tout ou rien
- 🚨 **Gestion d'erreurs** : Messages d'erreur clairs et appropriés

### Accessibilité
- 📡 **API REST** : Endpoints standardisés
- 📋 **Documentation** : Endpoints bien documentés
- 🔌 **Format** : JSON pour requêtes et réponses

---

## 5️⃣ CAS D'UTILISATION

### CU1 : Créer un compte
**Acteur** : Client  
**Préconditions** : Client existant dans le système  
**Étapes** :
1. Fournir l'ID client, type de compte et solde initial
2. Système génère un numéro de compte unique
3. Compte créé avec solde initialisé
4. Retour confirmation avec détails du compte

### CU2 : Effectuer un dépôt
**Acteur** : Client  
**Préconditions** : Compte existant  
**Étapes** :
1. Fournir ID compte, montant, libellé
2. Système valide montant > 0
3. Solde augmenté, transaction créée (CREDIT)
4. Retour confirmation

### CU3 : Effectuer un retrait
**Acteur** : Client  
**Préconditions** : Compte existant  
**Étapes** :
1. Fournir ID compte, montant, libellé
2. Système valide montant > 0 et solde suffisant
3. Solde diminué, transaction créée (DEBIT)
4. Retour confirmation (ou erreur si solde insuffisant)

### CU4 : Effectuer un virement
**Acteur** : Client  
**Préconditions** : Deux comptes existants, solde suffisant  
**Étapes** :
1. Fournir ID compte source, destination, montant, libellé
2. Système valide comptes ≠ et solde source suffisant
3. Débit compte source, crédit compte destination
4. 2 transactions générées (une par compte)
5. Retour confirmation

### CU5 : Consulter l'historique
**Acteur** : Client  
**Préconditions** : Compte existant  
**Étapes** :
1. Fournir ID compte
2. Système retourne liste triée (récent → ancien)
3. Montrant type, montant, date, description

---

## 6️⃣ API REST - Endpoints

### 🧑 Clients
```
POST   /api/clients                      → Créer un client
GET    /api/clients                      → Lister tous les clients
GET    /api/clients/{id}                 → Consulter un client
DELETE /api/clients/{id}                 → Supprimer un client
```

### 🏦 Comptes
```
POST   /api/comptes                      → Ouvrir un compte
GET    /api/comptes/client/{client_id}   → Lister comptes d'un client
GET    /api/comptes/{compteId}/solde     → Consulter solde
```

### 💳 Transactions
```
POST   /api/operations/depot             → Effectuer un dépôt
POST   /api/operations/retrait            → Effectuer un retrait
POST   /api/operations/virement           → Effectuer un virement
GET    /api/operations/releve/{compteId}  → Historique des opérations
```

---

## 7️⃣ MODÈLE DE DONNÉES

### Client
```java
- id (Long)
- name (String)
- prenom (String)
- email (String)
- telephone (String)
- comptes (List<Compte>)
```

### Compte
```java
- id (Long)
- numeroCompte (String) - unique, IBAN
- solde (BigDecimal)
- dateOuverture (LocalDate)
- typeCompte (String) - COURANT, EPARGNE, etc.
- client (Client)
- transactions (List<Transaction>)
```

### Transaction
```java
- id (Long)
- montant (BigDecimal)
- dateTransaction (LocalDateTime)
- typeTransaction (String) - CREDIT, DEBIT, VIREMENT_EMIS, VIREMENT_RECU
- description (String)
- compte (Compte)
```

---

## 8️⃣ EXEMPLES DE REQUÊTES/RÉPONSES

### 1️⃣ POST /api/clients
**Request** :
```json
{
  "name": "Dupont",
  "prenom": "Jean",
  "email": "jean.dupont@email.com",
  "telephone": "0612345678"
}
```
**Response** (201 Created) :
```json
{
  "id": 1,
  "name": "Dupont",
  "prenom": "Jean",
  "email": "jean.dupont@email.com",
  "telephone": "0612345678"
}
```

### 2️⃣ POST /api/comptes
**Request** :
```json
{
  "client_id": 1,
  "typeCompte": "COURANT",
  "soldeInitial": 1000.00
}
```
**Response** (201 Created) :
```json
{
  "id": 101,
  "numeroCompte": "FR7612345678901234567890123",
  "solde": 1000.00,
  "dateOuverture": "2026-04-12",
  "typeCompte": "COURANT"
}
```

### 3️⃣ POST /api/operations/depot
**Request** :
```json
{
  "compteId": 101,
  "montant": 500.00,
  "libelle": "Salaire mensuel"
}
```
**Response** (201 Created) : 200 OK (pas de body, ou confirmation)

### 4️⃣ GET /api/operations/releve/101
**Response** :
```json
[
  {
    "id": 1,
    "montant": 500.00,
    "dateTransaction": "2026-04-12T10:30:00",
    "typeTransaction": "CREDIT",
    "description": "Salaire mensuel (dépôt)"
  },
  {
    "id": 2,
    "montant": 100.00,
    "dateTransaction": "2026-04-11T14:20:00",
    "typeTransaction": "DEBIT",
    "description": "Retrait espèces (retrait)"
  }
]
```

---

## 9️⃣ GESTION DES ERREURS

| Code | Erreur | Exemple |
|------|--------|---------|
| 400 | Bad Request | Montant invalide ou négatif |
| 404 | Not Found | Client/Compte/Transaction inexistant |
| 500 | Server Error | Erreur base de données |

**Format erreur** :
```json
{
  "error": "Solde insuffisant",
  "message": "La solde du compte 101 est insufisant pour retirer 5000.00"
}
```

---

## 🔟 TECHNOLOGIES

- **Backend** : Spring Boot 3.5.7
- **ORM** : JPA/Hibernate
- **Base de données** : MySQL
- **Validation** : Jakarta Validation
- **Java** : JDK 17
- **Build** : Maven

---

## 1️⃣1️⃣ DÉPLOIEMENT ET TESTS

- ✅ Application standalone JAR
- ✅ Test unitaires (JUnit)
- ✅ Test d'intégration
- ✅ Postman collection fournie

---

**Date** : 12 Avril 2026  
**Cours** : ICT304 - Gestion Bancaire  
**État** : ✅ Complèt et fonctionnel
