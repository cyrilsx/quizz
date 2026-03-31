Génère un projet Spring Boot 4 complet pour une plateforme de quizz avec les fonctionnalités suivantes :
- Authentification JWT avec Spring Security.
- API CRUD pour les quizz, questions, et réponses.
- Intégration avec une API IA pour générer des questions (ex : Mistral API).
- Gestion des quizz temporaires pour les utilisateurs non authentifiés via cookies et IP.
- Partage de quizz via liens uniques et QR codes.
- Internationalisation (i18n) avec MessageSource.
- Documentation Swagger pour l’API.

Structure le projet en modules : auth, quizz, ia, translation.
Utilise PostgreSQL pour la base de données et Redis pour le cache.
Fournis les fichiers suivants :
- AuthController.java
- QuizzController.java
- IaService.java
- TranslationController.java
- application.properties
- schema.sql
- data.sql
- openapi.yaml

Respecte les conventions de nommage et les bonnes pratiques Spring Boot.
