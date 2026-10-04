# Deployment

The project deploys to [Render](https://render.com) from images pushed by GitHub Actions to GHCR.

## Pipeline

```
push/merge to main → CI (build + tests) → Deploy workflow:
  1. Build & push backend image (Jib)  → ghcr.io/cyrilsx/quizz-backend:<sha> + :latest
  2. Build & push frontend image (buildx) → ghcr.io/cyrilsx/quizz-frontend:<sha> + :latest
  3. Trigger Render deploys via API (if configured)
```

## GitHub configuration

Secrets (Settings → Secrets and variables → Actions → Secrets):

| Secret | Purpose |
|---|---|
| `JWT_SECRET` | CI tests and backend runtime |
| `MISTRAL_API_KEY` | CI tests and backend runtime |
| `RENDER_API_KEY` | Render API — triggers deploys |

Variables (Settings → Secrets and variables → Actions → Variables):

| Variable | Purpose |
|---|---|
| `RENDER_BACKEND_SERVICE_ID` | Render service ID (`svc-...`) of the backend |
| `RENDER_FRONTEND_SERVICE_ID` | Render service ID of the frontend |
| `API_URL` | Public backend URL baked into the frontend image at build time, e.g. `https://quizz-backend.onrender.com/api` |

The Render deploy step skips with a warning (images still push) when `RENDER_API_KEY` or a service ID is missing.

## Render configuration

Services are declared in `render.yaml` (blueprint). Create them in the Render dashboard via
New → Blueprint, pointing at this repository, or manually:

- **quizz-backend**: image `ghcr.io/cyrilsx/quizz-backend:latest`, port 8080,
  health check path `/actuator/health`.
  Environment: `DB_URL` (Neon JDBC URL, `sslmode=require`), `DB_USERNAME`, `DB_PASSWORD`,
  `JWT_SECRET`, `MISTRAL_API_KEY`, `CORS_ORIGINS` (frontend URL).
- **quizz-frontend**: image `ghcr.io/cyrilsx/quizz-frontend:latest`, port 80.

GHCR images are private by default: either make the packages public
(GitHub profile → Packages → package → Package settings → Change visibility) or add a
GitHub PAT with `read:packages` as a registry credential in Render.

## Frontend API URL

`API_URL` is baked into the frontend image at build time (`frontend/Dockerfile` build-arg,
substituted into `environment.prod.ts` via `fileReplacements` in `angular.json`).
Changing it requires re-running the Deploy workflow after updating the variable.

## Health check

`GET /actuator/health` → `{"status":"UP"}` (no auth; only this actuator endpoint is exposed).
