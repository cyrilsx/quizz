# Quizz Platform - Guide de Développement

## Objectif
Créer une plateforme de quizz avec Spring Boot (backend) et Angular (frontend), incluant :
- Authentification JWT.
- Génération de questions via IA (optionnel).
- Persistance PostgreSQL.
- Quizz temporaires pour utilisateurs non authentifiés.
- Partage via liens et QR codes.
- Internationalisation (i18n).

## Architecture
Voir [architecture.md](architecture.md) pour les détails.

## Étapes de Développement

### 1. Backend (Spring Boot 4)
- **Modules** :
  - `auth/` : Gestion des utilisateurs (JWT).
  - `quizz/` : CRUD pour les quizz.
  - `ia/` : Appel à l’API Mistral.
  - `translation/` : Gestion des traductions.
- **Configuration** :
  - `application.properties` : DB, JWT secret, URL Mistral API.
- **Tests** :
  - JUnit 5 pour les services.

### 2. Frontend (Angular 22.2)
- **Modules** :
  - `core/auth/` : Service d’authentification.
  - `features/quizz/` : Composants pour créer/participer aux quizz.
  - `shared/` : Composants réutilisables.
- **Librairies** :
  - Angular Material, RxJS, NgRx (optionnel), ngx-translate.
- **Tests** :
  - Jasmine/Karma.

### 3. Base de Données
- Schéma PostgreSQL : migrations Flyway dans `backend/src/main/resources/db/migration/` (appliquées automatiquement au démarrage).
- Utilisateur admin initial créé par la migration Java `V2__SeedAdminUser` (mot de passe via `ADMIN_PASSWORD`).

### 4. Déploiement
- **Local** :
  ```bash
  docker-compose up -d
