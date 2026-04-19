# 🔐 GUIDE D'AUTHENTIFICATION JWT

## Vue d'ensemble

L'application utilise **JWT (JSON Web Tokens)** pour sécuriser l'API REST. Chaque utilisateur doit s'inscrire et se connecter pour obtenir un token qui lui permet d'accéder aux endpoints protégés.

---

## 📋 Endpoints d'Authentification

### 1️⃣ Inscription (Register)
**Endpoint** : `POST /api/auth/register`  
**Accès** : Public (aucun token requis)

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

**Validations** :
- ✅ Nom : 2-100 caractères
- ✅ Prénom : 2-100 caractères
- ✅ Email : format email valide, unique
- ✅ Téléphone : 10 chiffres
- ✅ Username : 3-20 caractères, unique, alphanumériques + tirets/underscores
- ✅ Password : min 6 caractères
- ✅ Password == PasswordConfirm

---

### 2️⃣ Connexion (Login)
**Endpoint** : `POST /api/auth/login`  
**Accès** : Public (aucun token requis)

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

---

## 🔑 Utilisation du Token

Chaque requête vers un endpoint protégé **doit inclure** le token JWT dans le header `Authorization` :

```
Authorization: Bearer <token>
```

### Exemple avec curl :
```bash
curl -X GET http://localhost:8080/api/clients \
  -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJqZWFu..."
```

### Exemple avec Postman :
1. Aller dans l'onglet **Headers**
2. Ajouter une clé : `Authorization`
3. Valeur : `Bearer <votre_token>`

---

## 📊 Endpoints Protégés vs Publics

### ✅ Endpoints Publics (sans token)
```
POST   /api/auth/register         → Inscription
POST   /api/auth/login            → Connexion
GET    /api/auth/test             → Test API
```

### 🔒 Endpoints Protégés (token requis)
```
GET    /api/clients               → Lister clients
POST   /api/clients               → Créer client
GET    /api/clients/{id}          → Consulter client
DELETE /api/clients/{id}          → Supprimer client

POST   /api/comptes               → Ouvrir compte
GET    /api/comptes/client/{id}   → Lister comptes
GET    /api/comptes/{id}/solde    → Consulter solde

POST   /api/operations/depot      → Effectuer dépôt
POST   /api/operations/retrait    → Effectuer retrait
POST   /api/operations/virement   → Effectuer virement
GET    /api/operations/releve/{id} → Historique
```

---

## 🎯 Flux d'Authentification Complet

### 1️⃣ Inscription du Client
```
POST /api/auth/register
┌─────────────────────────────────────────┐
│ Client envoie : name, prenom, email,    │
│ telephone, username, password           │
└──────────────┬──────────────────────────┘
               │
               ▼
           Validation
         (uniqueness check)
               │
               ▼
        ✅ Client créé
        Mot de passe hashé
```

### 2️⃣ Connexion du Client
```
POST /api/auth/login
┌─────────────────────────────────────┐
│ Client envoie :                     │
│ username, password                  │
└──────────────┬──────────────────────┘
               │
               ▼
      Vérifier identifiants
               │
               ▼
    Mot de passe valide ?
               │
        ┌──────┴──────┐
       NON            OUI
        │              │
        ▼              ▼
     ❌ Erreur    🔐 Générer JWT
                       │
                       ▼
                 ✅ Retourner Token
```

### 3️⃣ Utilisation du Token
```
GET /api/clients
+ Header: Authorization: Bearer <token>
         │
         ▼
    Valider Token
         │
    ┌────┴─────────┐
  VALIDE        INVALIDE
    │              │
    ▼              ▼
 ✅ Accès    ❌ 401 Unauthorized
```

---

## 🛡️ Sécurité - Caractéristiques

| Aspect | Description |
|--------|-------------|
| **Hachage** | BCrypt pour les mots de passe |
| **Token** | JWT HS512 (HMAC SHA-512) |
| **Expiration** | 24 heures |
| **Transport** | HTTP Header Authorization |
| **Session** | Stateless (pas de session serveur) |
| **CORS** | Activé |
| **CSRF** | Désactivé (API REST) |

---

## 📝 Codes d'Erreur d'Authentification

| Code | Erreur | Solution |
|------|--------|----------|
| 400 | Validation échouée | Vérifier les champs requis |
| 401 | Token invalide/expiré | Se reconnecter pour obtenir un nouveau token |
| 403 | Accès refusé | Vérifier les permissions |
| 409 | Conflit (username/email existe) | Utiliser des identifiants uniques |

---

## 🔄 Workflow - Diagramme de Cas d'Usage

```
┌─────────────────────────────────────────────────────────┐
│                    APPLICATION BANCAIRE                 │
├────────────────────────────────────────────────────────┤
│                                                         │
│  ┌─────────────┐         ┌─────────────┐               │
│  │  NOUVEAU    │         │   CLIENT    │               │
│  │  CLIENT     │────────▶│   EXISTANT  │               │
│  └──────┬──────┘         └─────┬───────┘               │
│         │                       │                      │
│         │                       │                      │
│         ▼                       ▼                      │
│    ┌─────────────────┐  ┌─────────────────┐           │
│    │  /auth/register │  │   /auth/login   │           │
│    │                 │  │                 │           │
│    │ 📝 S'inscrire   │  │ 🔑 Se connecter │           │
│    └────────┬────────┘  └────────┬────────┘           │
│             │                    │                    │
│             ▼                    ▼                    │
│            ✅ Compte créé    🔐 JWT Token            │
│             │                    │                    │
│             └────────┬───────────┘                    │
│                      │                                │
│                      ▼                                │
│           ┌─────────────────────────────┐             │
│           │    Utiliser le Token        │             │
│           │  Authorization: Bearer ...  │             │
│           └────────────┬────────────────┘             │
│                        │                              │
│        ┌───────────────┼───────────────┐              │
│        ▼               ▼               ▼              │
│   ┌──────────┐  ┌─────────────┐ ┌─────────────┐      │
│   │ Clients  │  │  Comptes    │ │ Transactions│      │
│   └──────────┘  └─────────────┘ └─────────────┘      │
│                                                       │
└─────────────────────────────────────────────────────┘
```

---

## 💡 Tips & Bonnes Pratiques

✅ **À faire** :
- Stocker le token dans un endroit sûr (localStorage, sessionStorage)
- Ajouter le token dans chaque requête protégée
- Gérer l'expiration du token et se reconnecter si nécessaire
- Utiliser HTTPS en production pour éviter l'interception du token
- Hasher les mots de passe côté serveur

❌ **À éviter** :
- Publier le token en URL
- Stocker le token en cookies non-sécurisés
- Ignorer l'expiration du token
- Envoyer le token en clair sur HTTP
- Stocker les mots de passe en plain text

---

## 🧪 Test Rapide

### 1️⃣ Inscrire un client
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Dupont",
    "prenom": "Jean",
    "email": "jean@example.com",
    "telephone": "0612345678",
    "username": "jean123",
    "password": "Password456!",
    "passwordConfirm": "Password456!"
  }'
```

### 2️⃣ Se connecter
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "jean123",
    "password": "Password456!"
  }'
```

### 3️⃣ Utiliser le token
```bash
curl -X GET http://localhost:8080/api/clients \
  -H "Authorization: Bearer <token_reçu>"
```

---

**Documentation créée le** : 12 Avril 2026  
**État** : ✅ En production
