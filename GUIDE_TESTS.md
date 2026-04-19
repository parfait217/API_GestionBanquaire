# 🧪 GUIDE COMPLET DES TESTS - Gestion Bancaire

## 📋 Types de Tests Implémentés

| Type | Fichier | Couverture |
|------|---------|-----------|
| **Unitaires** | `AuthServiceTest.java` | Service d'authentification |
| **Unitaires** | `ClientServiceTest.java` | Service client |
| **Intégration** | `AuthControllerTest.java` | Contrôleur d'authentification |

---

## 🚀 Option 1 : TESTS MANUELS AVEC POSTMAN (⭐ Recommandé pour démarrage)

### Étape 1️⃣ : Démarrer l'application
```bash
# Terminal 1 - Démarrer l'app
cd c:\Users\JEREMIE\Documents\ICTL3\S2\ICT304\Gestion-Bancaire
mvn spring-boot:run
```

### Étape 2️⃣ : Importer la collection Postman
1. Ouvrir **Postman**
2. `File` → `Import`
3. Sélectionner `Postman_Collection.json`
4. ✅ Collection chargée

### Étape 3️⃣ : Tester les endpoints

#### 🔐 **AUTHENTIFICATION (Tests Publics - Pas de token)**

**Test 1 : Endpoint Public**
```
GET http://localhost:8080/api/auth/test
Response: "API Banque - Endpoint public" ✅
```

**Test 2 : S'inscrire**
```
POST http://localhost:8080/api/auth/register
Body:
{
  "name": "Dupont",
  "prenom": "Jean",
  "email": "jean.dupont@example.com",
  "telephone": "0612345678",
  "username": "jeandupont",
  "password": "SecurePassword123!",
  "passwordConfirm": "SecurePassword123!"
}

Response (201 Created):
{
  "id": 1,
  "name": "Dupont",
  "email": "jean.dupont@example.com",
  "username": "jeandupont",
  "actif": true
}
```

**Test 3 : Se connecter (IMPORTANT - Récupérer le token)**
```
POST http://localhost:8080/api/auth/login
Body:
{
  "username": "jeandupont",
  "password": "SecurePassword123!"
}

Response (200 OK):
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "type": "Bearer",
  "clientId": 1,
  "username": "jeandupont",
  "email": "jean.dupont@example.com"
}

⚠️ COPIER LE TOKEN POUR LES REQUÊTES SUIVANTES
```

#### 🧑 **CLIENTS (Tests Protégés - Token Requis)**

**Dans Postman :**
1. Aller dans **Variables**
2. Insérer le token dans la variable `{{token}}`

**Test 4 : Lister tous les clients**
```
GET http://localhost:8080/api/clients
Header: Authorization: Bearer {{token}}

Response (200 OK):
[
  {
    "id": 1,
    "name": "Dupont",
    "email": "jean.dupont@example.com",
    "username": "jeandupont",
    "actif": true
  }
]
```

**Test 5 : Consulter un client**
```
GET http://localhost:8080/api/clients/1
Header: Authorization: Bearer {{token}}

Response (200 OK):
{
  "id": 1,
  "name": "Dupont",
  ...
}
```

#### 🏦 **COMPTES (Tests Protégés)**

**Test 6 : Ouvrir un compte**
```
POST http://localhost:8080/api/comptes
Header: Authorization: Bearer {{token}}
Body:
{
  "client_id": 1,
  "typeCompte": "COURANT",
  "soldeInitial": 1000.00
}

Response (201 Created):
{
  "id": 101,
  "numeroCompte": "FR7612345...",
  "solde": 1000.00,
  "typeCompte": "COURANT"
}
```

**Test 7 : Lister les comptes**
```
GET http://localhost:8080/api/comptes/client/1
Header: Authorization: Bearer {{token}}

Response (200 OK):
[
  {
    "id": 101,
    "numeroCompte": "FR7612345...",
    "solde": 1000.00,
    "typeCompte": "COURANT"
  }
]
```

#### 💳 **TRANSACTIONS (Tests Protégés)**

**Test 8 : Effectuer un dépôt**
```
POST http://localhost:8080/api/operations/depot
Header: Authorization: Bearer {{token}}
Body:
{
  "compteId": 101,
  "montant": 500.00,
  "libelle": "Salaire"
}

Response (201 Created):
{
  "success": true,
  "message": "Dépôt réussi"
}
```

**Test 9 : Effectuer un retrait**
```
POST http://localhost:8080/api/operations/retrait
Header: Authorization: Bearer {{token}}
Body:
{
  "compteId": 101,
  "montant": 100.00,
  "libelle": "Retrait espèces"
}

Response (201 Created):
{
  "success": true,
  "message": "Retrait réussi"
}
```

**Test 10 : Effectuer un virement**
```
POST http://localhost:8080/api/operations/virement
Header: Authorization: Bearer {{token}}
Body:
{
  "compteSource": 101,
  "compteDestination": 102,
  "montant": 250.00,
  "libelle": "Virement épargne"
}

Response (201 Created):
{
  "success": true,
  "message": "Virement réussi"
}
```

**Test 11 : Consulter l'historique**
```
GET http://localhost:8080/api/operations/releve/101
Header: Authorization: Bearer {{token}}

Response (200 OK):
[
  {
    "id": 1,
    "montant": 500.00,
    "dateTransaction": "2026-04-12T10:30:00",
    "typeTransaction": "CREDIT",
    "description": "Dépôt - Salaire"
  },
  ...
]
```

---

## 🧪 Option 2 : TESTS AUTOMATISÉS AVEC MAVEN

### Exécuter TOUS les tests
```bash
mvn test
```

### Exécuter une classe de test spécifique
```bash
mvn test -Dtest=AuthServiceTest
mvn test -Dtest=ClientServiceTest
mvn test -Dtest=AuthControllerTest
```

### Exécuter une méthode de test spécifique
```bash
mvn test -Dtest=AuthServiceTest#testRegisterSuccess
```

### Exécuter avec rapport de couverture
```bash
mvn test jacoco:report
```
Rapport généré dans : `target/site/jacoco/index.html`

---

## 🟢 Résultats Attendus

### Tests Réussis
```
[INFO] Tests run: 13, Failures: 0, Errors: 0, Skipped: 0, Time: 2.345s
[INFO] BUILD SUCCESS
```

### Test Détaillé
```
[INFO] Running com.example.banque.services.AuthServiceTest
[INFO] Tests run: 3, Failures: 0, Errors: 0
[INFO] ✓ Inscription réussie d'un nouveau client
[INFO] ✓ Inscription échouée si username existe
[INFO] ✓ Connexion réussie avec JWT
```

---

## 🔍 Cas de Test Implémentés

### AuthServiceTest (3 tests)
```
✅ testRegisterSuccess
   - Conditions : Request valide, username unique
   - Résultat : Client créé avec mot de passe hashé

✅ testRegisterFailureUsernameExists
   - Conditions : Username déjà existant
   - Résultat : Exception levée

✅ testLoginSuccess
   - Conditions : Identifiants corrects
   - Résultat : JWT token généré et retourné
```

### ClientServiceTest (4 tests)
```
✅ testGetAllClients
   - Récupère la liste complète des clients
   
✅ testGetClientById
   - Récupère un client spécifique par ID
   
✅ testDeleteClient
   - Supprime un client
   
✅ testFindByUsername
   - Cherche un client par username unique
```

### AuthControllerTest (3 tests)
```
✅ testPublicEndpoint
   - Endpoint public sans authentification
   
✅ testRegisterSuccess
   - Inscription via HTTP POST (201 Created)
   
✅ testLoginSuccess
   - Connexion via HTTP POST (200 OK)
```

---

## 💡 Validation des Erreurs

### ❌ Token Manquant
```bash
curl http://localhost:8080/api/clients

Response (401 Unauthorized):
{
  "error": "Unauthorized",
  "message": "Token d'authentification manquant"
}
```

### ❌ Token Invalide
```bash
curl -H "Authorization: Bearer invalid_token" \
     http://localhost:8080/api/clients

Response (401 Unauthorized):
{
  "error": "Unauthorized",
  "message": "Token invalide ou expiré"
}
```

### ❌ Montant Négatif
```bash
POST /api/operations/depot
Body: { "compteId": 1, "montant": -100, "libelle": "Test" }

Response (400 Bad Request):
{
  "success": false,
  "message": "Le montant doit être positif"
}
```

### ❌ Solde Insuffisant
```bash
POST /api/operations/retrait
Body: { "compteId": 1, "montant": 99999, "libelle": "Test" }

Response (400 Bad Request):
{
  "success": false,
  "message": "Solde insuffisant"
}
```

---

## 📊 Flux Complet de Test

```
1️⃣  Démarrer l'application
    └─ mvn spring-boot:run
    
2️⃣  Ouvrir Postman
    └─ Importer Postman_Collection.json
    
3️⃣  Test Public
    └─ GET /api/auth/test ✅
    
4️⃣  Inscription
    └─ POST /api/auth/register ✅
    └─ Récupérer clientId (1)
    
5️⃣  Connexion
    └─ POST /api/auth/login ✅
    └─ Copier token JWT
    
6️⃣  Variables Postman
    └─ Insérer token dans {{token}}
    
7️⃣  Tests Protégés
    └─ GET /api/clients ✅
    └─ POST /api/comptes ✅
    └─ POST /api/operations/depot ✅
    └─ POST /api/operations/retrait ✅
    └─ GET /api/operations/releve ✅
    
8️⃣  Tests Manuels JUnit
    └─ mvn test ✅
    └─ Tous les tests passent
```

---

## 🎯 Checklist de Validation

- [ ] Application démarre sans erreur
- [ ] Endpoint public accessible (`/api/auth/test`)
- [ ] Inscription crée un client avec username unique
- [ ] Connexion retourne un JWT valide
- [ ] Endpoints protégés refusent requête sans token
- [ ] Dépôt augmente le solde
- [ ] Retrait diminue le solde
- [ ] Virement transfère entre comptes
- [ ] Historique affiche les transactions
- [ ] Tests JUnit passent tous
- [ ] Validations rejettent données invalides
- [ ] Logging affiche contexte opérations

---

## 🔧 Dépannage

### Erreur : "Connection refused"
```
❌ Application ne démarre pas
✅ Solution : mvn clean spring-boot:run
```

### Erreur : "401 Unauthorized"
```
❌ Token manquant ou expiré
✅ Solution : Reconnecter-vous et copier nouveau token
```

### Erreur : "403 Forbidden"
```
❌ Accès refusé malgré token valide
✅ Solution : Vérifier les permissions
```

### Tests échouent
```bash
# Nettoyer et relancer
mvn clean test

# Avec debug
mvn test -X
```

---

## 📚 Documentation de Référence

- **[CAHIER_CHARGES.md](CAHIER_CHARGES.md)** - Spécifications complètes
- **[JWT_AUTHENTICATION_GUIDE.md](JWT_AUTHENTICATION_GUIDE.md)** - Guide d'authentification
- **[Postman_Collection.json](Postman_Collection.json)** - Collection API

---

**Date** : 12 Avril 2026  
**Dernière Mise à Jour** : Tests complets implémentés  
**État** : ✅ **Prêt pour Tests**
