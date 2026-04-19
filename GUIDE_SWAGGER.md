# 📚 GUIDE SWAGGER/OPENAPI - Documentation Interactive

## ✨ C'est quoi Swagger ?

**Swagger** (maintenant appelé **OpenAPI 3**) est une documentation **interactive** et **automatique** de votre API REST. À la place d'écrire de la doc en Markdown, Swagger la génère directement depuis les annotations de votre code ! 🚀

### Avantages de Swagger 🎯
- ✅ **Documentation auto-générée** - Mise à jour automatiquement avec le code
- ✅ **Testable directement** - Essayez les endpoints sans Postman
- ✅ **Schemas JSON** - Reçoit/envoie JSON structuré
- ✅ **Authentification JWT** - Gère les tokens Bearer automatiquement
- ✅ **Lisible par machines** - Les outils externes peuvent lire la doc

---

## 🚀 DÉMARRER L'APPLICATION

### Étape 1️⃣ : Compiler et Démarrer
```bash
# Terminal - Aller au répertoire du projet
cd c:\Users\JEREMIE\Documents\ICTL3\S2\ICT304\Gestion-Bancaire

# Compiler
mvn clean install

# Démarrer l'application
mvn spring-boot:run
```

**Résultat attendu :**
```
[INFO] Started BanqueApplication in 4.5 seconds
[INFO] Swagger UI available at: http://localhost:8080/swagger-ui.html
```

### Étape 2️⃣ : Ouvrir Swagger UI
Ouvrir votre navigateur et aller à :
```
http://localhost:8080/swagger-ui.html
```

**Vous verrez:**
```
🏦 API Gestion Bancaire
Version 2.0

Documentation complète de l'API REST de gestion bancaire avec authentification JWT

Swagger UI | OpenAPI JSON
```

---

## 📖 Interface Swagger - Les Différentes Sections

### 1️⃣ Entête avec Titre et Description
```
┌─────────────────────────────────────────┐
│ 🏦 API Gestion Bancaire v2.0           │
│                                         │
│ Documentation complète de l'API REST    │
│ Authentification JWT requise            │
└─────────────────────────────────────────┘
```

### 2️⃣ Sélecteur de Serveur
```
Servers: 
[Dropdown] http://localhost:8080/
```

### 3️⃣ Catégories d'Endpoints (Tags)
```
🔐 Authentification
🧑 Gestion des Clients
🏦 Comptes Bancaires
💳 Transactions Financières
```

### 4️⃣ Chaque Endpoint
```
POST /api/auth/register
[Blue] 🔐 Authentification
Description: Créer un nouveau compte client avec email et username uniques
Parameters: none
Request Body: RegisterRequest
Responses: 201 Created, 400 Bad Request
```

---

## 🔑 AUTHENTIFICATION SWAGGER - Activer le Bearer Token

### Étape 1️⃣ : Se Connecter d'Abord
1. Développer **POST /api/auth/login** 
2. Cliquer **"Try it out"**
3. Remplir le body :
```json
{
  "username": "jeandupont",
  "password": "SecurePassword123!"
}
```
4. Cliquer **"Execute"**
5. Copier le `token` de la réponse

### Étape 2️⃣ : Autoriser Swagger
1. En haut à droite de Swagger, cliquer sur le bouton **"Authorize"** 🔒
2. Une fenêtre popup s'ouvre
3. Insérer : `Bearer <token_reçu>`
   ```
   Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJqZWFuZHVwb250I...
   ```
4. Cliquer **"Authorize"**
5. ✅ Vouz êtes maintenant authentifiés !

### Étape 3️⃣ : Les Requêtes Protégées Passent Maintenant
Tous les endpoints avec 🔒 **@SecurityRequirement** fonctionnent maintenant :
```
GET    /api/clients                  ✅ Fonctionne
GET    /api/comptes/client/1         ✅ Fonctionne
POST   /api/operations/depot         ✅ Fonctionne
```

---

## 🧪 TESTER LES ENDPOINTS DANS SWAGGER

### 🔐 AUTHENTIFICATION (Pas besoin de token)

#### 1️⃣ Test Public
```
GET /api/auth/test

Click: "Try it out" [Execute]

Response (200):
"Réponse publique - API fonctionnelle"
```

#### 2️⃣ S'inscrire
```
POST /api/auth/register

Click: "Try it out"

Request Body:
{
  "name": "Dupont",
  "prenom": "Jean",
  "email": "jean.dupont@example.com",
  "telephone": "0612345678",
  "username": "jeandupont",
  "password": "SecurePassword123!",
  "passwordConfirm": "SecurePassword123!"
}

[Execute]

Response (201 Created):
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

#### 3️⃣ Se Connecter (Récupérer Token)
```
POST /api/auth/login

Click: "Try it out"

Request Body:
{
  "username": "jeandupont",
  "password": "SecurePassword123!"
}

[Execute]

Response (200):
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",  ← COPIER CE TOKEN
  "type": "Bearer",
  "clientId": 1,
  "username": "jeandupont",
  "email": "jean.dupont@example.com",
  "message": "Connexion réussie"
}

⚠️ CLIQUER SUR "Authorize" ET INSÉRER LE TOKEN
```

### 🧑 CLIENTS (Protégé - Token Requis)

#### 4️⃣ Lister Tous les Clients
```
GET /api/clients

Click: "Try it out" [Execute]

Response (200):
[
  {
    "id": 1,
    "name": "Dupont",
    "email": "jean.dupont@example.com",
    ...
  }
]
```

#### 5️⃣ Consulter un Client
```
GET /api/clients/1

Click: "Try it out"
Path Parameter: id = 1
[Execute]

Response (200):
{
  "id": 1,
  "name": "Dupont",
  ...
}
```

### 🏦 COMPTES (Protégé)

#### 6️⃣ Ouvrir un Compte
```
POST /api/comptes

Click: "Try it out"

Request Body:
{
  "client_id": 1,
  "typeCompte": "COURANT",
  "soldeInitial": 1000.00
}

[Execute]

Response (201):
{
  "id": 101,
  "numeroCompte": "FR7612345...",
  "solde": 1000.00,
  "typeCompte": "COURANT",
  "dateOuverture": "2026-04-12"
}
```

#### 7️⃣ Lister les Comptes d'un Client
```
GET /api/comptes/client/1

Click: "Try it out"
Path Parameter: client_id = 1
[Execute]

Response (200):
[
  {
    "id": 101,
    "numeroCompte": "FR7612345...",
    "solde": 1000.00,
    ...
  }
]
```

#### 8️⃣ Consulter le Solde
```
GET /api/comptes/101/solde

Click: "Try it out"
Path Parameter: compteId = 101
[Execute]

Response (200):
1000.00
```

### 💳 TRANSACTIONS (Protégé)

#### 9️⃣ Effectuer un Dépôt
```
POST /api/operations/depot

Click: "Try it out"

Request Body:
{
  "compteId": 101,
  "montant": 500.00,
  "libelle": "Salaire"
}

[Execute]

Response (201):
(empty - 201 Created success)
```

#### 🔟 Effectuer un Retrait
```
POST /api/operations/retrait

Click: "Try it out"

Request Body:
{
  "compteId": 101,
  "montant": 100.00,
  "libelle": "Retrait espèces"
}

[Execute]

Response (201):
(empty - 201 Created success)
```

#### 1️⃣1️⃣ Effectuer un Virement
```
POST /api/operations/virement

Click: "Try it out"

Request Body:
{
  "compteSource": 101,
  "compteDestination": 102,
  "montant": 250.00,
  "libelle": "Virement épargne"
}

[Execute]

Response (201):
(empty - 201 Created success)
```

#### 1️⃣2️⃣ Consulter l'Historique
```
GET /api/operations/releve/101

Click: "Try it out"
Path Parameter: compteId = 101
[Execute]

Response (200):
[
  {
    "id": 1,
    "montant": 500.00,
    "dateTransaction": "2026-04-12T10:30:00",
    "typeTransaction": "CREDIT",
    "description": "Dépôt - Salaire"
  },
  {
    "id": 2,
    "montant": 100.00,
    "dateTransaction": "2026-04-12T11:15:00",
    "typeTransaction": "DEBIT",
    "description": "Retrait - Espèces"
  }
]
```

---

## 📊 Affichage des Réponses dans Swagger

### Format Réussi (200/201)
```
✅ Status: 200 OK
   Content-Type: application/json
   
   {
     "id": 1,
     "name": "Dupont",
     ...
   }
```

### Format Erreur (400/401/404)
```
❌ Status: 400 Bad Request
   Content-Type: application/json
   
   {
     "success": false,
     "message": "Montant invalide",
     "errorCode": "INVALID_ARGUMENT"
   }
```

---

## 📄 Trois URLs Principales

| URL | Contenu | Usage |
|-----|---------|-------|
| `http://localhost:8080/swagger-ui.html` | **Interface interactive** | Tester l'API |
| `http://localhost:8080/v3/api-docs` | **JSON OpenAPI complet** | Intégrations externes |
| `http://localhost:8080/v3/api-docs.yaml` | **YAML OpenAPI** | Documentation alternative |

---

## 🔧 Exporter la Documentation

### Télécharger le JSON OpenAPI
```bash
# Via navigateur
curl http://localhost:8080/v3/api-docs > api-docs.json

# Puis l'utiliser dans :
# - Postman (Import)
# - Insomnia
# - API clients externes
```

### Télécharger le YAML
```bash
curl http://localhost:8080/v3/api-docs.yaml > api-docs.yaml
```

---

## 💡 Conseils d'Utilisation

### ✅ À Faire
- ✅ Utiliser Swagger pour **découvrir l'API** rapidement
- ✅ Tester les **endpoints protégés** en autorisant d'abord
- ✅ Vérifier les **schemas** de réponse
- ✅ Utiliser pour les **démos** et présentations

### ❌ À Éviter
- ❌ Ne pas mettre en prod sans HTTPS
- ❌ Ne pas exposer les secrets dans les exemples
- ❌ Ne pas désactiver la sécurité d'authentification

---

## 🎯 Flux Complet

```
1️⃣  Démarrer l'app
    └─ mvn spring-boot:run

2️⃣  Ouvrir Swagger
    └─ http://localhost:8080/swagger-ui.html

3️⃣  Lire les Catégories
    └─ 🔐 Authentification
    └─ 🧑 Clients
    └─ 🏦 Comptes
    └─ 💳 Transactions

4️⃣  S'Authentifier
    └─ POST /api/auth/login
    └─ Copier token
    └─ Cliquer "Authorize"

5️⃣  Tester Endpoints
    └─ Try it out
    └─ Execute
    └─ Vérifier réponse

6️⃣  Exporter si Besoin
    └─ Copier JSON/YAML
    └─ Utiliser dans Postman/outils
```

---

## 🐛 Dépannage

### ❌ Swagger n'apparaît pas
```
Solution 1: Vérifier dépendance dans pom.xml
  └─ <groupId>org.springdoc</groupId>
     <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>

Solution 2: Relancer l'app
  └─ mvn clean spring-boot:run
```

### ❌ "Authorize" ne fonctionne pas
```
Solution: Token peut être mal formé
  └─ Vérifier "Bearer " avant le token
  └─ Pas d'espaces ou caractères spéciaux
```

### ❌ Endpoint protégé retourne 401
```
Solution: Token expiré (24h)
  └─ Se reconnecter
  └─ Récupérer nouveau token
```

---

## 📚 Ressources

- **Swagger/OpenAPI Officiel** : https://swagger.io
- **SpringDoc** : https://springdoc.org
- **OpenAPI Spec** : https://spec.openapis.org/oas/v3.0.3

---

**Date** : 12 Avril 2026  
**Swagger Version** : 3.0 (OpenAPI)  
**SpringDoc Version** : 2.3.0  
**État** : ✅ **Prêt à Utiliser**
