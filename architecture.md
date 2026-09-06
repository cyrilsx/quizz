---

### **B. Architecture.md**

```markdown
# Architecture Technique

## Backend
- **Technologies** : Spring Boot 4, Java 25, Spring Security, PostgreSQL, Redis.
- **Endpoints** (exemples) :
  - `POST /api/auth/login` → JWT.
  - `GET /api/quizzes` → Liste des quizz publics.
  - `POST /api/quizzes/{id}/generate-questions` → Appel IA.
  - `GET /api/translations?key={key}` → Traduction.
- **Modules** :
  - `auth/` : Gestion des utilisateurs.
  - `quizz/` : CRUD pour les quizz.
  - `ia/` : Appel à l’API Mistral.
  - `translation/` : Gestion des traductions.

## Frontend
- **Technologies** : Angular 22.2, Material Design, NgRx (optionnel), ngx-translate.
- **Composants** :
  - `QuizzCreatorComponent` : Interface pour créer un quizz.
  - `QuizzPlayerComponent` : Interface pour passer un quizz.
  - `QuizzShareComponent` : Interface pour partager un quizz.
- **Services** :
  - `AuthService` : Gestion de l’authentification.
  - `TranslateService` : Gestion des traductions.

## Base de Données
- **Tables** : users, quizzes, questions, answers, shares, temp_sessions.
- **Relations** :
  - users 1 → N quizzes.
  - quizzes 1 → N questions.
  - questions 1 → N answers.

## Sécurité
- **JWT** : Expire en 24h.
- **Quizz temporaires** :
  - Stocker l’IP et un cookie `quizz-limit=5` côté frontend.
  - Bloquer après 5 quizz non authentifiés.

## IA (Optionnel)
- **Endpoint** : `POST /api/ia/generate-questions` → Retourne 5 questions/réponses.
- **Rate Limiting** : 5 appels/jour par utilisateur.

## Internationalisation
- **Frontend** : Utilisation de ngx-translate pour gérer les traductions.
- **Backend** : Utilisation de MessageSource pour gérer les messages traduits.
